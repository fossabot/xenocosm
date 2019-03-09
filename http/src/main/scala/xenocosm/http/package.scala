package xenocosm

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import galaxique.data.{Galaxy, Star, Universe}
import org.http4s.{Charset, MediaType}
import org.http4s.headers.`Content-Type`
import squants.space.{AstronomicalUnits, KiloParsecs, Parsecs}

package object http {
  val jsonHal = `Content-Type`(MediaType.`application/hal+json`, Charset.`UTF-8`)

  val ⎈ = UuidSegment
  val ✺ = Point3Segment(Universe.scale, KiloParsecs)
  val ✨ = Point3Segment(Galaxy.scale, Parsecs)
  val ★ = Point3Segment(Star.scale, AstronomicalUnits)

  def eitherT[A](f:A => IO[Unit]):A => EitherT[IO, XenocosmError, A] =
    a => EitherT(f(a).attempt).map(_ => a).leftMap(WrappedThrowable.apply)

  def eitherT[A](option:IO[Option[A]])(left:XenocosmError):EitherT[IO, XenocosmError, A] =
    EitherT(option.map({
      case None => Left(left)
      case Some(a) => Right(a)
    }).recover({ case err => Left(WrappedThrowable(err)) }))
}
