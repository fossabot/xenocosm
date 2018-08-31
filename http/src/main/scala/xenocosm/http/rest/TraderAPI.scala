package xenocosm.http
package rest

import java.util.UUID
import cats.data.{EitherT, OptionT}
import cats.effect.IO
import cats.implicits._
import org.http4s.AuthedService
import org.http4s.circe._
import org.http4s.dsl.io._
import org.log4s.Logger
import spire.random.{Dist, Generator}

import xenocosm._
import xenocosm.data.{Identity, Trader}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.response.{TraderResponse, TradersResponse}
import xenocosm.http.services.DataStore

final class TraderAPI(val auth:XenocosmAuthentication, val data:DataStore, val gen:Generator) {
  import io.circe.syntax._
  import cavernetics.FSM.syntax._
  import Identity.instances._
  import Trader.instances._
  import TraderResponse.instances._
  import TradersResponse.instances._

  private val logger:Logger = org.log4s.getLogger

  private val deselectTrader:Identity => UUID => Either[XenocosmError, Identity] = identity => traderID =>
    if(identity.trader.exists(_.uuid === traderID)) {
      logger.info(s"current trader deselected: $traderID")
      identity.transition(TraderDeselected)
    } else {
      Right(identity)
    }

  private val createFromUpdated:Identity => IO[Identity] = identity =>
    for {
      _ <- data.createTrader(identity)
      _ <- data.updateIdentity(identity)
    } yield identity

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {
    // List Traders for an Identity
    case GET -> Root as identity ⇒
      data.listTraders(identity)
        .flatMap({ traders =>
          Ok(TradersResponse(traders).asJson, jsonHal).map(auth.withAuthToken(identity))
        })

    // Create and select a Trader
    case POST -> Root as identity ⇒
      EitherT.right[XenocosmError](IO.pure(TraderCreated(Dist[Trader].apply(gen))))
        .subflatMap(identity.transition)
        .semiflatMap(createFromUpdated)
        .value.flatMap({
          case Right(updated) if updated.trader.isDefined =>
            logger.info(s"new trader created: ${updated.trader.map(_.uuid.toString).getOrElse("empty")}")
            Created(TraderResponse(updated.trader.get).asJson, jsonHal).map(auth.withAuthToken(updated))
          case Right(updated) =>
            logger.error(s"no trader created???: $updated")
            NoContent().map(auth.withAuthToken(updated))
          case Left(err) =>
            logger.error(s"unexpected error: $err")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Show a Trader
    case GET -> Root / UuidSegment(traderID) as identity ⇒
      data.selectTrader(identity.uuid, traderID)
        .flatMap({
          case Some(trader) =>
            logger.debug(s"trader found: $traderID")
            Ok(TraderResponse(trader).asJson, jsonHal).map(auth.withAuthToken(identity))
          case None =>
            logger.info(s"no trader found: $traderID")
            NotFound().map(auth.withAuthToken(identity))
        })

    // Select a Trader
    case POST -> Root / UuidSegment(traderID) as identity ⇒
      OptionT(data.selectTrader(identity.uuid, traderID))
        .map(TraderSelected)
        .toRight(NoTraderSelected)
        .subflatMap(identity.transition)
        .semiflatMap(x => data.updateIdentity(x).map(_ => x))
        .value.flatMap({
          case Right(updated) if updated.trader.isDefined =>
            logger.info(s"trader selected: $traderID")
            Ok(TraderResponse(updated.trader.get).asJson, jsonHal).map(auth.withAuthToken(updated))
          case Right(updated) =>
            logger.error(s"no trader selected???: $updated")
            NotFound().map(auth.withAuthToken(updated))
          case Left(NoTraderSelected) =>
            logger.info(s"no trader found: $traderID")
            NotFound().map(auth.withAuthToken(identity))
          case Left(err) =>
            logger.error(s"unexpected error: $err")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Delete and deselect a Trader
    case DELETE -> Root / UuidSegment(traderID) as identity ⇒
      data.deleteTrader(identity.uuid, traderID)
        .map(_ => deselectTrader(identity)(traderID))
        .flatMap({
          case Right(updated) =>
            logger.error(s"trader deleted: $traderID")
            NoContent().map(auth.withAuthToken(updated))
          case Left(err) =>
            logger.error(s"unexpected error: $err")
            InternalServerError().map(auth.withAuthToken(identity))
        })
  }
}
