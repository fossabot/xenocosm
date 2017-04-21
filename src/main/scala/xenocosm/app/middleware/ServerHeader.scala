package xenocosm
package app
package middleware

import org.http4s._

object ServerHeader {
  private val header:Header = Header("Server", s"${XenocosmBuild.name}/${XenocosmBuild.version} http4s/${BuildInfo.version}")

  def wrap(service:HttpService):HttpService = Service.lift {
    req ⇒ service(req).map {
      case Status.Successful(resp) ⇒ resp.putHeaders(header)
      case Status.ClientError(resp) ⇒ resp.putHeaders(header)
      case Status.ServerError(resp) ⇒ resp.putHeaders(header)
      case resp ⇒ resp
    }
  }
}
