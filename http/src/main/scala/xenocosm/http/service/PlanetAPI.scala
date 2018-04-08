package xenocosm.http
package service

import cats.effect.IO
import io.circe.syntax._
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.io._

import galaxique.data._
import galaxique.SparseSpace3
import xenocosm.http.data.PlanetResponse

object PlanetAPI extends XenocosmAPI {
  import Galaxy.instances._
  import PlanetResponse.instances._
  import SparseSpace3.syntax._
  import Star.instances._
  import Universe.instances._

  val service = HttpService[IO] {
    case GET -> Root / ♠(uuid) / ♣(locU) / ♥(locG) / ♦(locS) ⇒
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
