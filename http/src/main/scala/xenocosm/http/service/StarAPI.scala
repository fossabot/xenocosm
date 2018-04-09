package xenocosm.http
package service

import cats.effect.IO
import io.circe.syntax._
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.io._
import squants.space.AstronomicalUnits

import galaxique.data.{Galaxy, Point3, Universe}
import galaxique.SparseSpace3
import xenocosm.http.data.StarResponse

object StarAPI extends XenocosmAPI {
  import Galaxy.instances._
  import SparseSpace3.syntax._
  import StarResponse.instances._
  import Universe.instances._

  val service = HttpService[IO] {
    case GET -> Root / "v1" / "multiverse" / ⎈(uuid) / ✺(locU) / ✨(locG) ⇒
      val range = AstronomicalUnits(1)
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
  }
}
