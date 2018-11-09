package xenocosm.http
package rest

import cats.data.EitherT
import cats.effect.IO
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

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {

    // List Traders for an Identity
    case GET -> Root as identity ⇒
      EitherT(data.listTraders(identity).attempt)
        .leftMap(WrappedThrowable.apply)
        .value.flatMap({
          case Right(traders) =>
            logger.debug(s"identity has ${traders.size} traders")
            Ok(TradersResponse(traders).asJson, jsonHal).map(auth.withAuthToken(identity))
          case Left(WrappedThrowable(throwable)) =>
            logger.error(throwable)("wrapped throwable")
            InternalServerError().map(auth.withAuthToken(identity))
          case x =>
            logger.error(s"unexpected result: $x")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Create and select a Trader
    case POST -> Root as identity ⇒
      EitherT.right[XenocosmError](IO.pure(TraderCreated(Dist[Trader].apply(gen))))
        .subflatMap(identity.transition)
        .flatMap(eitherT(data.createTrader))
        .flatMap(eitherT(data.updateIdentity))
        .value.flatMap({
          case Right(updated) if updated.trader.isDefined =>
            logger.debug(s"new trader created: ${updated.trader.map(_.uuid.toString).getOrElse("empty")}")
            Created(TraderResponse(updated.trader.get).asJson, jsonHal).map(auth.withAuthToken(updated))
          case Left(WrappedThrowable(throwable)) =>
            logger.error(throwable)("wrapped throwable")
            InternalServerError().map(auth.withAuthToken(identity))
          case x =>
            logger.error(s"unexpected result: $x")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Show a Trader
    case GET -> Root / UuidSegment(traderID) as identity ⇒
      eitherT(data.selectTrader(identity.uuid, traderID))(NoTraderSelected)
        .value.flatMap({
          case Right(trader) =>
            logger.debug(s"trader found: $traderID")
            Ok(TraderResponse(trader).asJson, jsonHal).map(auth.withAuthToken(identity))
          case Left(NoTraderSelected) =>
            logger.info(s"no trader found: $traderID")
            NotFound().map(auth.withAuthToken(identity))
          case Left(WrappedThrowable(throwable)) =>
            logger.error(throwable)("wrapped throwable")
            InternalServerError().map(auth.withAuthToken(identity))
          case x =>
            logger.error(s"unexpected result: $x")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Select a Trader
    case POST -> Root / UuidSegment(traderID) as identity ⇒
      eitherT(data.selectTrader(identity.uuid, traderID))(NoTraderSelected)
        .map(TraderSelected)
        .subflatMap(identity.transition)
        .flatMap(eitherT(data.updateIdentity))
        .value.flatMap({
          case Right(updated) if updated.trader.isDefined =>
            logger.debug(s"trader selected: $traderID")
            Ok(TraderResponse(updated.trader.get).asJson, jsonHal).map(auth.withAuthToken(updated))
          case Left(NoTraderSelected) =>
            logger.info(s"no trader found: $traderID")
            NotFound().map(auth.withAuthToken(identity))
          case Left(WrappedThrowable(throwable)) =>
            logger.error(throwable)("wrapped throwable")
            InternalServerError().map(auth.withAuthToken(identity))
          case x =>
            logger.error(s"unexpected result: $x")
            InternalServerError().map(auth.withAuthToken(identity))
        })

    // Delete and deselect a Trader
    case DELETE -> Root / UuidSegment(traderID) as identity ⇒
      EitherT.right(data.deleteTrader(identity.uuid, traderID))
        .subflatMap(_ => identity.transition(TraderDeselected(traderID)))
        .flatMap(eitherT(data.updateIdentity))
        .value.flatMap({
          case Right(updated) =>
            logger.debug(s"trader deleted: $traderID")
            NoContent().map(auth.withAuthToken(updated))
          case Left(WrappedThrowable(throwable)) =>
            logger.error(throwable)("wrapped throwable")
            InternalServerError().map(auth.withAuthToken(identity))
          case x =>
            logger.error(s"unexpected result: $x")
            InternalServerError().map(auth.withAuthToken(identity))
        })
  }
}
