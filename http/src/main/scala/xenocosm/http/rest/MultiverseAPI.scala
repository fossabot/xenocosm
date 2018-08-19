package xenocosm.http
package rest

import cats.effect.IO
import io.circe.Json
import io.circe.syntax._
import org.http4s.AuthedService
import org.http4s.circe._
import org.http4s.dsl.io._
import galaxique.SparseSpace3
import galaxique.data._

import xenocosm.data.Trader
import xenocosm.http.response._

object MultiverseAPI {
  import MultiverseResponse.instances._
  import UniverseResponse.instances._
  import GalaxyResponse.instances._
  import StarResponse.instances._
  import PlanetResponse.instances._
  import Universe.instances._
  import SparseSpace3.syntax._
  import Galaxy.instances._
  import Star.instances._

  val service:AuthedService[Trader, IO] = AuthedService[Trader, IO] {
    case GET -> Root as trader ⇒
      Ok(MultiverseResponse.asJson, jsonHal)

    case GET -> Root / ⎈(uuid) as trader ⇒
      val range = Universe.scale
      val response = UniverseResponse(Universe(uuid), Point3.zero, range)
      Ok(response.asJson, jsonHal)

    case GET -> Root / ⎈(uuid) / ✺(locU) as trader ⇒
      val range = Galaxy.scale
      val located = for {
        galaxy <-  Universe(uuid).locate(locU)
      } yield galaxy

      located match {
        case Some(galaxy) =>
          val response = GalaxyResponse(galaxy, Point3.zero, range)
          Ok(response.asJson, jsonHal)
        case None =>
          NotFound(Json.Null, jsonHal)
      }

    case GET -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) as trader ⇒
      val range = Star.scale
      val located = for {
        galaxy <-  Universe(uuid).locate(locU)
        star <- galaxy.locate(locG)
      } yield star

      located match {
        case Some(star) =>
          val response = StarResponse(star, Point3.zero, range)
          Ok(response.asJson, jsonHal)
        case None =>
          NotFound(Json.Null, jsonHal)
      }

    case GET -> Root / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) as trader ⇒
      val located = for {
        galaxy <-  Universe(uuid).locate(locU)
        star <- galaxy.locate(locG)
        planet <- star.locate(locS)
      } yield planet

      located match {
        case Some(planet) =>
          val response = PlanetResponse(planet)
          Ok(response.asJson, jsonHal)
        case None =>
          NotFound(Json.Null, jsonHal)
      }
  }
}
