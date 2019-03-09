package xenocosm.http
package rest

import cats.effect.IO
import cats.implicits._
import io.circe.syntax._
import org.http4s.{AuthedService, Uri}
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.headers.Location
import org.log4s.Logger

import galaxique.data._
import xenocosm._
import xenocosm.data.{CosmicLocation, Identity, Trader}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.response._
import xenocosm.http.services.DataStore

final class MultiverseAPI(val auth:XenocosmAuthentication, val data:DataStore) {
  import cavernetics.FSM.syntax._
  import CosmicLocationResponse.instances._
  import Universe.instances._
  import Galaxy.instances._
  import Star.instances._
  import Planet.instances._
  import Trader.instances._
  import ErrorResponse.instances._

  private val logger:Logger = org.log4s.getLogger

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {

    // Show Current Universe
    case GET -> Root / ⎈(uuid) as identity ⇒
      val loc = CosmicLocation(uuid, None, None, None)
      identity.trader match {
        case Some(trader) if trader.ship.loc.universe === loc.universe =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    // Show Current Galaxy
    case GET -> Root / ⎈(uuid) / ✺(locU) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), None, None)
      identity.trader match {
        case Some(trader) if trader.ship.loc.galaxy === loc.galaxy =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    // Show Current Star
    case GET -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), Some(locG), None)
      identity.trader match {
        case Some(trader) if trader.ship.loc.star === loc.star =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    // Show Current Planet
    case GET -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))
      identity.trader match {
        case Some(trader) if trader.ship.loc.planet === loc.planet =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    // Move to Planet
    case POST -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))
      identity.trader match {
        case Some(trader) =>
          trader transition ShipMoved(loc) match {
            case Right(updated) =>
              data.updateTrader(identity, updated)
              val response = CosmicLocationResponse(loc)
              Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
            case Left(WrappedThrowable(throwable)) =>
              logger.error(throwable)("Unexpected exception")
              InternalServerError().map(auth.withAuthToken(identity))
            case Left(error) =>
              val response = ErrorResponse(trader, error)
              Forbidden(response.asJson, jsonHal).map(auth.withAuthToken(identity))
          }
        case _ =>
          SeeOther(Location(Uri.unsafeFromString("/trader"))).map(auth.withAuthToken(identity))
      }
  }
}
