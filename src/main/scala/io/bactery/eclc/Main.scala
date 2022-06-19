package io.bactery.ecqlc

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) = {
    ECQLCServer.stream[IO].compile.drain.as(ExitCode.Success)
  }
}
