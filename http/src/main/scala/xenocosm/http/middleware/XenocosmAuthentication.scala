package xenocosm.http
package middleware

import java.time.{Clock, Instant}
import java.util.UUID
import scala.concurrent.duration._
import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import cats.implicits._
import io.circe.Json
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server._
import org.reactormonk.{CryptoBits, PrivateKey}

import xenocosm.data.Identity
import xenocosm.http.response.apiCurie
import xenocosm.http.services.DataStore

final class XenocosmAuthentication(val key:PrivateKey, val data:DataStore) {
  private val crypto:CryptoBits = CryptoBits(key)
  private val clock:Clock = Clock.systemUTC
  private val cookieDuration:FiniteDuration = 5.minutes
  private def expiration:Option[HttpDate] = HttpDate.fromInstant(Instant.now(clock).plusSeconds(cookieDuration.toSeconds)).toOption

  val toCookie:Identity => Cookie =
    identity => Cookie(
      name = XenocosmAuthentication.cookieName,
      content = crypto.signToken(identity.uuid.toString, clock.millis.toString),
      expires = expiration,
      httpOnly = true
    )

  val withAuthToken:Identity => Response[IO] => Response[IO] =
    identity => _.addCookie(toCookie(identity))

  private val getIdentity:UUID => IO[Either[String, Identity]] =
    data.selectIdentity(_).map({
      case Some(identity) => Right(identity)
      case _ => Left("Identity not found")
    })

  private val getUUID:Request[IO] => Either[String, UUID] = request =>
    for {
      header <- headers.Cookie.from(request.headers).toRight("missing cookies")
      cookie <- header.values.toList.find(_.name == XenocosmAuthentication.cookieName).toRight("missing auth cookie")
      token <- crypto.validateSignedToken(cookie.content).toRight("invalid cookie signature")
      message <- Either.catchNonFatal(UUID.fromString(token)).leftMap(_ => "invalid UUID")
    } yield message

  val authUserFromRequest:Kleisli[IO, Request[IO], Either[String, Identity]] =
    Kleisli(getUUID.andThen({
      case Right(identityID) => getIdentity(identityID)
      case Left(x) => IO.pure(Left(x))
    }))

  private val onFailure:AuthedService[String, IO] =
    Kleisli(request => OptionT.liftF(Forbidden(XenocosmAuthentication.errorResponse(request.authInfo), jsonHal)))

  private val middleware:AuthMiddleware[IO, Identity] = AuthMiddleware(authUserFromRequest, onFailure)

  val wrap:AuthedService[Identity, IO] => HttpService[IO] = middleware
}

object XenocosmAuthentication {
  val cookieName:String = "xenoAuth"

  def apply(keyStr:String, data:DataStore):XenocosmAuthentication = {
    val key:PrivateKey = PrivateKey(scala.io.Codec.toUTF8(keyStr))
    new XenocosmAuthentication(key, data)
  }

  val errorResponse:String => Json = message => Json.obj(
    "_links" -> Json.obj(
      "curies" -> Json.arr(apiCurie),
      "api:authentication" -> "/auth".asJson
    ),
    "error" -> message.asJson
  )
}
