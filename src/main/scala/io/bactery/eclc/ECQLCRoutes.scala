package io.bactery.ecqlc

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import io.bactery.eclc.helpers.JsonHelpers
import io.bactery.eclc.services.{IPService, PunchLineService, WeatherService}

object ECQLCRoutes extends JsonHelpers {

  def weatherRoutes[F[_]: Sync](ips: IPService[F], weathers: WeatherService[F], punchLineService: PunchLineService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case req @ GET -> Root / "weather"  =>
        req.remoteAddr match {
          case Some(address) =>
            println(address.toUriString)
            for {
              ip <- ips.get(address.toUriString)
              weather <- weathers.get(ip)
              punchLine <- punchLineService.getPunchLine(weather)
              resp <- punchLine match {
                case Right(pL) => Ok(pL)
                case Left(err) => Conflict(err)
              }
            } yield resp
          case _ => Conflict("Couldn't get Location :/")
        }
    }
  }
}