package io.bactery.ecqlc

import cats.effect.Async
import com.github.benmanes.caffeine.cache.Caffeine
import fs2.Stream
import io.bactery.eclc.models.{IP, Weather}
import io.bactery.eclc.services.config.Config
import io.bactery.eclc.services.{CacheService, IPService, PunchLineService, WeatherService}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.{CORS, Logger}
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import scalacache.Entry
import scalacache.caffeine.CaffeineCache


object ECQLCServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    implicit val ipCache: CaffeineCache[F, String, IP] = CaffeineCache[F, String, IP](underlying = Caffeine.newBuilder.build[String, Entry[IP]]())
    implicit val weatherCache: CaffeineCache[F, String, Weather] = CaffeineCache[F, String, Weather](underlying = Caffeine.newBuilder.build[String, Entry[Weather]]())
    for {
      client <- BlazeClientBuilder[F].stream
      config = ConfigSource.default.loadOrThrow[Config]
      cacheService = CacheService.impl[F]
      ipAlg = IPService.impl[F](client, cacheService)
      punchlineService = PunchLineService.impl[F]
      weatherAlg = WeatherService.impl[F](client, cacheService, config)
      weatherRoutes = ECQLCRoutes.weatherRoutes[F](ipAlg, weatherAlg, punchlineService)

      cors = CORS.policy
        .withAllowOriginAll
        .withAllowCredentials(false)
        .apply(weatherRoutes) // <+>

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        cors
      ).orNotFound



      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- BlazeServerBuilder[F]
          .bindHttp(8080, "0.0.0.0")
          .withHttpApp(finalHttpApp)
          .serve
    } yield exitCode
  }.drain
}
