package io.bactery.eclc.services

import cats.Applicative
import cats.implicits._
import scalacache._
import scalacache.modes.sync._

import scala.concurrent.duration._

trait CacheService[F[_]] {

  def get[T](key: String)(implicit cache: Cache[T]): F[Option[T]]

  def putInCache[T](key: String, item: T)(implicit c: Cache[T]): F[T]

}

object CacheService {

  def apply[F[_]](implicit ev: CacheService[F]): CacheService[F] = ev

  def impl[F[_]: Applicative]: CacheService[F] = new CacheService[F] {

    override def get[T](address: String)(implicit cache: Cache[T]): F[Option[T]] = sync.get(address)(cache, mode, Flags.defaultFlags).pure[F]

    override def putInCache[T](key: String, item: T)(implicit c: Cache[T]): F[T] = sync.caching[T](key)(ttl = Some(1.hour)) { item }.pure[F]
  }
}