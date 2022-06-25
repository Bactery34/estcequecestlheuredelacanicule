package io.bactery.eclc.services

import cats.effect.Sync
import cats.implicits._
import io.bactery.eclc.models.{IP, Weather}
import scalacache.caffeine.CaffeineCache

import scala.concurrent.duration._

trait CacheService[F[_]] {

  val ipCache: F[CaffeineCache[F, String, IP]]

  val weatherCache: F[CaffeineCache[F, String, Weather]]

  def get[T](key: String)(cache: CaffeineCache[F, String, T]): F[Option[T]]

  def putInCache[T](key: String, item: T)(c: CaffeineCache[F, String, T]): F[T]

}

object CacheService {

  def apply[F[_]](implicit ev: CacheService[F]): CacheService[F] = ev

  def impl[F[_]: Sync]: CacheService[F] = new CacheService[F] {

    override val ipCache = CaffeineCache[F, String, IP]

    override val weatherCache = CaffeineCache[F, String, Weather]

    override def get[T](address: String)(cache: CaffeineCache[F, String, T]): F[Option[T]] = cache.doGet(address)

    override def putInCache[T](key: String, item: T)(cache: CaffeineCache[F, String, T]): F[T] = cache.doPut(key, item, ttl = Some(1.hour)).map(_ => item)
  }
}