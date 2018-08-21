package xenocosm.http

import cats.effect.IO
import org.http4s._

trait HttpCheck {

  def checkStatus(actual:IO[Response[IO]]):Status =
    actual.unsafeRunSync.status

  def checkBody[A](actual:IO[Response[IO]])(implicit ev: EntityDecoder[IO, A]):Option[A] = {
    val response = actual.unsafeRunSync

    if (response.body.compile.toVector.unsafeRunSync.isEmpty) {
      None
    } else {
      Some(response.as[A].unsafeRunSync)
    }
  }
}
