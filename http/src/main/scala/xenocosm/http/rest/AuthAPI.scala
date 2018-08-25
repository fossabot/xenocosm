package xenocosm.http
package rest

import cats.data.OptionT
import cats.effect.IO
import org.http4s.{BasicCredentials, Challenge, HttpService, Request, Response, Uri}
import org.http4s.dsl.io._
import org.http4s.headers.{`WWW-Authenticate`, Authorization, Location}

import xenocosm.data.{ForeignID, Identity}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.DataStore

final class AuthAPI(val auth:XenocosmAuthentication, val data:DataStore) {

  private val success:Identity => IO[Response[IO]] = identity => SeeOther(Location(Uri.uri("/"))).map(auth.withAuthToken(identity))
  private val failure:IO[Response[IO]] = Unauthorized(`WWW-Authenticate`(Challenge("Basic", "Xenocosm", Map.empty[String, String])))

  private val basicAuth:Request[IO] => Option[ForeignID] = req =>
    for {
      authorization <- req.headers.get(Authorization)
      credentials <- BasicCredentials.unapply(authorization.credentials)
      ref <- Some(credentials.username) if credentials.username.nonEmpty
    } yield ForeignID(ref)

  private val selectOrCreate:ForeignID => IO[Identity] = ref =>
    data.selectIdentity(ref).flatMap({
      case None =>
        val identity = Identity(ref)
        data.createIdentity(identity).map(_ => identity)
      case Some(identity) => IO.pure(identity)
    })

  val service:HttpService[IO] = HttpService[IO] {
    case req @ POST -> Root ⇒
      OptionT(IO.pure(basicAuth(req)))
        .semiflatMap(selectOrCreate)
        .value.flatMap({
          case Some(identity) => success(identity)
          case None => failure
        })

    case req @ HEAD -> Root ⇒
      auth.authUserFromRequest(req).flatMap({
        case Right(identity) => NoContent().map(auth.withAuthToken(identity))
        case Left(_) => failure
      })

    case DELETE -> Root ⇒ NoContent()
  }
}
