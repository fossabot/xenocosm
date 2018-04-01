package xenocosm.http
package middleware

import cats.effect.IO
import org.http4s._
import xenocosm.{BuildInfo => XenocosmBuild}

object ServerHeader {
  private val header:Header = Header("Server", s"xenocosm/${XenocosmBuild.version}")

  def wrap(service:HttpService[IO]):HttpService[IO] = cats.data.Kleisli {
    req ⇒ service(req).map {
      case Status.Successful(resp) ⇒ resp.putHeaders(header)
      case Status.ClientError(resp) ⇒ resp.putHeaders(header)
      case Status.ServerError(resp) ⇒ resp.putHeaders(header)
      case resp ⇒ resp
    }
  }
}
