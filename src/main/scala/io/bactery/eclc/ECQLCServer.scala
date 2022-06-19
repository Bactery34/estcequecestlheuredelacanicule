package io.bactery.ecqlc

import cats.effect.Async
import fs2.Stream
import io.bactery.eclc.services.config.Config
import io.bactery.eclc.services.{CacheService, IPService, WeatherService}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.{CORS, Logger}
import pureconfig.ConfigSource
import pureconfig.generic.auto._


object ECQLCServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F].stream
      config = ConfigSource.default.loadOrThrow[Config]
      cacheService = CacheService.impl[F]
      ipAlg = IPService.impl[F](client, cacheService)
      weatherAlg = WeatherService.impl[F](client, cacheService, config)
      weatherRoutes = ECQLCRoutes.weatherRoutes[F](ipAlg, weatherAlg)

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
