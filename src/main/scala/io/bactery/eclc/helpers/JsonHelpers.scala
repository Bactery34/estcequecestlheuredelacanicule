package io.bactery.eclc.helpers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import io.circe.{Decoder, Encoder}

import scala.util.Try

trait JsonHelpers {

  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  implicit val dateEncoder: Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.format(formatter))
  implicit val dateDecoder: Decoder[LocalDateTime] = Decoder.decodeString.emapTry[LocalDateTime](str => {
    Try(LocalDateTime.parse(str, formatter))
  })
}
