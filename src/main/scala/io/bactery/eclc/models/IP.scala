package io.bactery.eclc.models

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

final case class IP(country: String, city: String)

object IP {
  implicit val ipDecoder : Decoder[IP] = (c: HCursor) => for {
    country <- c.downField("country").as[String]
    city <- c.downField("city").as[String]
  } yield IP(country, city)

  implicit def ipEntityDecoder[F[_] : Concurrent]: EntityDecoder[F, IP] =
    jsonOf

  implicit val ipEncoder: Encoder[IP] = deriveEncoder[IP]

  implicit def ipEntityEncoder[F[_]]: EntityEncoder[F, IP] =
    jsonEncoderOf
}