package io.bactery.ecqlc

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import io.bactery.eclc.models.Weather
import io.bactery.eclc.services.{IPService, WeatherService}

object ECQLCRoutes {

  def weatherRoutes[F[_]: Sync](ips: IPService[F], weathers: WeatherService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case req @ GET -> Root / "weather"  =>
        req.remoteAddr match {
          case Some(address) =>
            println(address.toUriString)
            for {
              maybeIp <- ips.get(address.toUriString)
              maybeWeather <- maybeIp match {
                case Right(ip) => weathers.get(ip.city)
                case Left(err) => Left(err).withRight[Weather].pure[F]
              }
              resp <- maybeWeather match {
                case Right(weather) => Ok(weather)
                case Left(err) => {
                  println("err")
                  Conflict(err)
                }
              }
            } yield resp
          case _ => Conflict("Couldn't get Location :/")
        }
    }
  }
}