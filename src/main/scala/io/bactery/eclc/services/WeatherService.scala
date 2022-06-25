package io.bactery.eclc.services

import cats.effect.Concurrent
import cats.implicits._
import io.bactery.eclc.models.{IP, Weather}
import io.bactery.eclc.services.config.Config
import org.http4s._
import org.http4s.client.Client
import scalacache.caffeine.CaffeineCache

trait WeatherService[F[_]]{
  def get(ip: Either[String, IP]):  F[Either[String, Weather]]
}

object WeatherService {
  
  def apply[F[_]](implicit ev: WeatherService[F]): WeatherService[F] = ev
  
  def impl[F[_]: Concurrent](R: Client[F], cacheService: CacheService[F], config: Config)(implicit cache: CaffeineCache[F, String, Weather]): WeatherService[F] = new WeatherService[F] {

    def get(ip: Either[String, IP]): F[Either[String, Weather]] = {
      ip match {
        case Right(value) =>
          for {
            maybeWeather <- cacheService.get[Weather](value.city)(cache)
            weather <- maybeWeather  match {
              case Some(weather) =>
                println("weather from cache : " + weather)
                Right(weather).withLeft[String].pure[F]
              case _ =>
                R.expectOption[Weather](Request.apply(uri = Uri.unsafeFromString(s"https://api.weatherapi.com/v1/current.json?key=${config.weather.apiKey}&aqi=no&q=${value.city}")))
                .flatMap {
                  case Some(weather) =>
                    println("weather from site : " + weather)
                    cacheService.putInCache[Weather](value.city, weather)(cache).map(Right(_).withLeft[String])
                  case _ => Left("Couldn't fetch weather from API, please try again :(").withRight[Weather].pure[F]
                }
            }
          } yield weather
        case Left(err) => Left(err).withRight[Weather].pure[F]
      }

    }
  }
}
