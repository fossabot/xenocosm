package xenocosm.http
package middleware

import java.time.Clock
import java.util.UUID
import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import cats.implicits._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server._
import org.reactormonk.{CryptoBits, PrivateKey}

import xenocosm.data.Trader
import xenocosm.http.services.DataStore

final class XenocosmAuthentication(val key:PrivateKey, val data:DataStore) {
  private val crypto:CryptoBits = CryptoBits(key)
  private val clock:Clock = Clock.systemUTC

  val toCookie:Trader => Cookie =
    trader => Cookie(XenocosmAuthentication.cookieName, crypto.signToken(trader.uuid.toString, clock.millis.toString))

  private val getTrader:UUID => IO[Either[String, Trader]] =
    data.selectTrader(_).map({
      case Some(trader) => Right(trader)
      case _ => Left("Trader not found")
    })

  private val getTraderID:Request[IO] => Either[String, UUID] = request =>
    for {
      header <- headers.Cookie.from(request.headers).toRight("missing cookies")
      cookie <- header.values.toList.find(_.name == XenocosmAuthentication.cookieName).toRight("missing auth cookie")
      token <- crypto.validateSignedToken(cookie.content).toRight("invalid cookie signature")
      message <- Either.catchNonFatal(UUID.fromString(token)).leftMap(_ => "invalid UUID")
    } yield message

  private val authUser:Kleisli[IO, Request[IO], Either[String, Trader]] =
    Kleisli(getTraderID.andThen({
      case Right(traderID) => getTrader(traderID)
      case Left(x) => IO.pure(Left(x))
    }))

  private val onFailure:AuthedService[String, IO] =
    Kleisli(request => OptionT.liftF(Forbidden(request.authInfo.asJson)))

  private val middleware:AuthMiddleware[IO, Trader] = AuthMiddleware(authUser, onFailure)

  val wrap:AuthedService[Trader, IO] => HttpService[IO] = middleware
}

object XenocosmAuthentication {
  val cookieName:String = "xenoAuth"

  def apply(keyStr:String, data:DataStore):XenocosmAuthentication = {
    val key:PrivateKey = PrivateKey(scala.io.Codec.toUTF8(keyStr))
    new XenocosmAuthentication(key, data)
  }
}
