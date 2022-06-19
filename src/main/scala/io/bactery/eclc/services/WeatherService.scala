package io.bactery.eclc.services

import cats.effect.Concurrent
import cats.implicits._
import io.bactery.eclc.models.Weather
import io.bactery.eclc.services.config.Config
import scalacache.Cache
import scalacache.caffeine.CaffeineCache
import org.http4s._
import org.http4s.client.Client

trait WeatherService[F[_]]{

  implicit val weatherCache: Cache[Weather] = CaffeineCache[Weather]

  def get(city: String):  F[Either[String, Weather]]
}

object WeatherService {
  
  def apply[F[_]](implicit ev: WeatherService[F]): WeatherService[F] = ev
  
  def impl[F[_]: Concurrent](R: Client[F], cacheService: CacheService[F], config: Config): WeatherService[F] = new WeatherService[F] {

    def get(city: String): F[Either[String, Weather]] = {
      cacheService.get[Weather](city).flatMap {
        case Some(weather) =>
          Right(weather).withLeft[String].pure[F]
        case _ =>
          R.expectOption[Weather](Request.apply(uri = Uri.unsafeFromString(s"https://api.weatherapi.com/v1/current.json?key=${config.weather.apiKey}&aqi=no&q=$city")))
            .flatMap {
              case Some(weather) => cacheService.putInCache[Weather](city, weather).map(Right(_).withLeft[String])
              case _ => Left("Couldn't fetch weather from API, please try again :(").withRight[Weather].pure[F]
            }
      }
    }
  }
}
