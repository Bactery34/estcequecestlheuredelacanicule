package io.bactery.eclc.services

import cats.effect.Concurrent
import cats.implicits._

/**
  * TODO: ADD PUNCH LINES FOR EVERY HOUR RANGES
  * @tparam F
  */

trait PunchLineService[F[_]]{

  def getPunchLine(hour: String):  F[String]
}

object PunchLineService {

  def apply[F[_]](implicit ev: PunchLineService[F]): PunchLineService[F] = ev

  def impl[F[_]: Concurrent]: PunchLineService[F] = new PunchLineService[F] {
    override def getPunchLine(hour: String): F[String] = "lol".pure[F]
  }
}
