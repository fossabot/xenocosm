package xenocosm.http
package rest

import cats.effect.IO
import cats.implicits._
import io.circe.syntax._
import org.http4s.AuthedService
import org.http4s.circe._
import org.http4s.dsl.io._
import galaxique.data._

import xenocosm.data.{CosmicLocation, Identity}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.response._
import xenocosm.http.services.DataStore

final class MultiverseAPI(val auth:XenocosmAuthentication, val data:DataStore) {
  import CosmicLocationResponse.instances._
  import Universe.instances._
  import Galaxy.instances._
  import Star.instances._
  import Planet.instances._

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {

    case GET -> Root / ⎈(uuid) as identity ⇒
      val loc = CosmicLocation(uuid, None, None, None)
      identity.trader match {
        case Some(trader) if trader.ship.loc.universe === loc.universe =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    case GET -> Root / ⎈(uuid) / ✺(locU) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), None, None)
      identity.trader match {
        case Some(trader) if trader.ship.loc.galaxy === loc.galaxy =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    case GET -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), Some(locG), None)
      identity.trader match {
        case Some(trader) if trader.ship.loc.star === loc.star =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }

    case GET -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) as identity ⇒
      val loc = CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))
      identity.trader match {
        case Some(trader) if trader.ship.loc.planet === loc.planet =>
          val response = CosmicLocationResponse(loc)
          Ok(response.asJson, jsonHal).map(auth.withAuthToken(identity))
        case _ =>
          Forbidden().map(auth.withAuthToken(identity))
      }
  }
}
