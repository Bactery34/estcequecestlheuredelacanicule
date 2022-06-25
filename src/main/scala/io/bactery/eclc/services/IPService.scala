package io.bactery.eclc.services

import cats.effect.Concurrent
import cats.implicits._
import io.bactery.eclc.models.IP
import scalacache.caffeine.CaffeineCache
//import org.http4s.Uri.Path
import org.http4s._
import org.http4s.client.Client

trait IPService[F[_]]{

  def get(address: String):  F[Either[String, IP]]
}

object IPService {

  def apply[F[_]](implicit ev: IPService[F]): IPService[F] = ev

  def impl[F[_]: Concurrent](R: Client[F], cacheService: CacheService[F])(implicit cache: CaffeineCache[F, String, IP]) = new IPService[F] {

    def get(address: String): F[Either[String, IP]] = {

      val add = if (address == "127.0.0.1") "81.64.18.84" else address
      cacheService.get[IP](add)(cache).flatMap {
        case Some(ip) =>
          println("ip from cache : " + ip)
          Right(ip).withLeft[String].pure[F]
        case _ =>
          R.expectOption[IP](Request.apply(uri = Uri.unsafeFromString(s"http://ip-api.com/json/$add?fields=country,city")))
          .flatMap {
            case Some(ip)  =>
              println("ip from site : " + ip)
              cacheService.putInCache[IP](add, ip)(cache).map(Right(_).withLeft[String])
            case _ => Left("Couldn't fetch location, please try again :(").withRight[IP].pure[F]
          }
      }
    }
  }
}
