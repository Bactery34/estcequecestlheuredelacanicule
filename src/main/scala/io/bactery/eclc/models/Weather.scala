package io.bactery.eclc.models

import java.time.LocalDateTime

import cats.effect.Concurrent
import io.bactery.eclc.helpers.JsonHelpers
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

final case class Weather(city: String, country: String,  temperatureC: BigDecimal, temperatureF: BigDecimal, time: LocalDateTime)

object Weather extends JsonHelpers {
  implicit val weatherDecoder: Decoder[Weather] = (c: HCursor) => for {
    name <- c.downField("location").downField("name").as[String]
    country <- c.downField("location").downField("country").as[String]
    time <- c.downField("location").downField("localtime").as[LocalDateTime]
    temperatureC <- c.downField("current").downField("temp_c").as[BigDecimal]
    temperatureF <- c.downField("current").downField("temp_f").as[BigDecimal]
  } yield Weather(name, country, temperatureC, temperatureF, time)

  implicit def weatherEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, Weather] =
    jsonOf

  implicit val weatherEncoder: Encoder[Weather] = deriveEncoder[Weather]
  implicit def weatherEntityEncoder[F[_]]: EntityEncoder[F, Weather] =
    jsonEncoderOf
}