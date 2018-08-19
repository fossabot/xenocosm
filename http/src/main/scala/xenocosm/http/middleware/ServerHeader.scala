package xenocosm.http
package middleware

import cats.data.Kleisli
import cats.effect.IO
import org.http4s._

import xenocosm.{BuildInfo => XenocosmBuild}

object ServerHeader {
  private val header:Header = Header("Server", s"xenocosm/${XenocosmBuild.version}")

  def wrap(service:HttpService[IO]):HttpService[IO] =
    Kleisli(service(_).map(_.putHeaders(header)))
}
