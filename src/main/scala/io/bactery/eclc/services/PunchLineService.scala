package io.bactery.eclc.services

import cats.effect.Sync
import io.bactery.eclc.helpers.PunchLineHelpers
import io.bactery.eclc.models.{TempRange, Weather}
import io.circe.Json

/**
  * TODO: ADD PUNCH LINES FOR EVERY HOUR RANGES
  * @tparam F
  */

trait PunchLineService[F[_]]{

  def getPunchLine(weather: Either[String, Weather]):  F[Either[String, Json]]
}

object PunchLineService extends PunchLineHelpers {

  def apply[F[_]](implicit ev: PunchLineService[F]): PunchLineService[F] = ev

  def impl[F[_]: Sync]: PunchLineService[F] = new PunchLineService[F] {
    override def getPunchLine(weather: Either[String, Weather]): F[Either[String, Json]] =
      Sync[F].delay(weather.map { value =>
        val pL = punchlineMaps(TempRange.getTrueTemp(value.time.getHour, value.temperatureC))
        Json.obj(
          "city" -> Json.fromString(value.city),
          "country" ->  Json.fromString(value.country),
          "temperature" -> Json.fromBigDecimal(value.temperatureC),
          "punchLine" -> Json.fromString(pL)
        )
      })
  }
}
