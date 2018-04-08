package xenocosm.http
package service

import cats.effect.IO
import io.circe.syntax._
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.io._
import squants.space.Parsecs

import galaxique.data.{Point3, Universe}
import galaxique.SparseSpace3
import xenocosm.http.data.GalaxyResponse

object GalaxyAPI extends XenocosmAPI {
  import Universe.instances._
  import GalaxyResponse.instances._
  import SparseSpace3.syntax._

  val service = HttpService[IO] {
    case GET -> Root / ♠(uuid) / ♣(locU) ⇒
      val range = Parsecs(1)

      Universe(uuid).locate(locU) match {
        case Some(galaxy) =>
          val response = GalaxyResponse(galaxy, Point3.zero, range)
          Ok(response.asJson, jsonHal)
        case None =>
          NotFound(Json.Null, jsonHal)
      }
  }
}
