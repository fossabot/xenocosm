package xenocosm.http
package rest

import cats.data.OptionT
import cats.effect.IO
import org.http4s.{BasicCredentials, Challenge, HttpService, Request, Response}
import org.http4s.dsl.io._
import org.http4s.headers.{`WWW-Authenticate`, Authorization, Location}
import org.log4s.Logger

import xenocosm.data.{ForeignID, Identity}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.DataStore

final class AuthAPI(val auth:XenocosmAuthentication, val data:DataStore) {
  import xenocosm.http.syntax.trader._

  private val failure:IO[Response[IO]] = Unauthorized(`WWW-Authenticate`(Challenge("Basic", "Xenocosm", Map.empty[String, String])))
  private val logger:Logger = org.log4s.getLogger

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
        logger.info(s"new identity created: ${identity.uuid}")
        data.createIdentity(identity).map(_ => identity)
      case Some(identity) =>
        IO.pure(identity)
    })

  val service:HttpService[IO] = HttpService[IO] {
    // Authenticate
    case req @ POST -> Root ⇒
      OptionT(IO.pure(basicAuth(req)))
        .semiflatMap(selectOrCreate)
        .value.flatMap({
          case Some(identity) if identity.trader.isDefined =>
            logger.debug(s"session created for identity ${identity.uuid}")
            SeeOther(Location(identity.trader.get.uri)).map(auth.withAuthToken(identity))
          case Some(identity) =>
            logger.debug(s"session created for identity ${identity.uuid}")
            SeeOther(Location(apiTrader)).map(auth.withAuthToken(identity))
          case None =>
            logger.info("no identity selected")
            failure
        })

    // Session heartbeat
    case req @ HEAD -> Root ⇒
      auth.authUserFromRequest(req).flatMap({
        case Right(identity) =>
          logger.debug(s"session exists for identity ${identity.uuid}")
          NoContent().map(auth.withAuthToken(identity))
        case Left(msg) =>
          logger.debug(s"session does not exist: $msg")
          failure
      })

    // Destroy session
    case DELETE -> Root ⇒
      logger.debug("Session destroyed")
      NoContent()
  }
}
