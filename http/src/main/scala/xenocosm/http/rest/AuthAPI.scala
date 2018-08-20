package xenocosm.http
package rest

import cats.data.OptionT
import cats.effect.IO
import org.http4s.{BasicCredentials, Challenge, HttpService, Request, Response, Uri}
import org.http4s.dsl.io._
import org.http4s.headers.{`WWW-Authenticate`, Authorization, Location}

import xenocosm.data.ForeignID
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.DataStore

final class AuthAPI(val auth:XenocosmAuthentication, val data:DataStore) {

  val success:IO[Response[IO]] = SeeOther(Location(Uri.uri("/")))
  val failure:IO[Response[IO]] = Unauthorized(`WWW-Authenticate`(Challenge("Basic", "Xenocosm", Map.empty[String, String])))

  val basicAuth:Request[IO] => Option[BasicCredentials] = req =>
    for {
      authorization <- req.headers.get(Authorization)
      credentials <- BasicCredentials.unapply(authorization.credentials)
    } yield credentials

  val service:HttpService[IO] = HttpService[IO] {
    case req @ POST -> Root ⇒
      OptionT(IO.pure(basicAuth(req)))
        .flatMapF(credentials => data.selectIdentity(ForeignID(credentials.username)))
        .value.flatMap({
          case Some(identity) => success.map(_.addCookie(auth.toCookie(identity)))
          case None => failure
        })
  }
}
