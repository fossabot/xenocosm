package xenocosm.http
package rest

import cats.data.{EitherT, OptionT}
import cats.effect.IO
import cats.implicits._
import org.http4s.AuthedService
import org.http4s.dsl.io._
import org.log4s.Logger
import spire.random.{Dist, Generator}

import xenocosm._
import xenocosm.data.{Identity, Trader}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.DataStore

final class TraderAPI(val auth:XenocosmAuthentication, val data:DataStore, val gen:Generator) {
  import cavernetics.FSM.syntax._
  import Identity.instances._
  import Trader.instances._

  private val logger:Logger = org.log4s.getLogger

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {
    // List Traders for an Identity
    case GET -> Root as identity ⇒
      data.listTraders(identity)
        .flatMap({ traders =>
          //FIXME: JSON-HAL Traders List
          Ok().map(auth.withAuthToken(identity))
        })

    // Create and select a Trader
    case POST -> Root as identity ⇒
      EitherT.right[XenocosmError](IO.pure(TraderCreated(Dist[Trader].apply(gen))))
        .subflatMap(identity.transition)
        .semiflatMap(x => data.updateIdentity(x).map(_ => x))
        .value.flatMap({
          case Right(updated) =>
            logger.info(s"new trader created: ${updated.trader.map(_.uuid.toString).getOrElse("empty")}")
            //FIXME: JSON-HAL Trader
            Created().map(auth.withAuthToken(updated))
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
            //FIXME: JSON-HAL Trader
            Ok().map(auth.withAuthToken(identity))
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
          case Right(updated) =>
            logger.info(s"trader selected: $traderID")
            //FIXME: JSON-HAL Trader
            Ok().map(auth.withAuthToken(updated))
          case Left(NoTraderSelected) =>
            logger.info(s"no trader found: $traderID")
            NotFound().map(auth.withAuthToken(identity))
          case Left(err) =>
            logger.error(s"unexpected error: $err")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Delete and unselect a Trader
    case DELETE -> Root / UuidSegment(traderID) as identity ⇒
      data.deleteTrader(identity.uuid, traderID)
        .map(_ =>
          if(identity.trader.exists(_.uuid === traderID)) {
            logger.error(s"trader unselected: $traderID")
            identity.transition(TraderUnselected)
          } else {
            Right(identity)
          }
        ).flatMap({
          case Right(updated) =>
            logger.error(s"trader deleted: $traderID")
            NoContent().map(auth.withAuthToken(updated))
          case Left(err) =>
            logger.error(s"unexpected error: $err")
            InternalServerError().map(auth.withAuthToken(identity))
        })
  }
}
