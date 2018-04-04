package xenocosm.http.service

import cats.effect.IO
import galaxique.data.Point3
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.io._
import squants.space.Parsecs
import xenocosm.http.data.UniverseResponse

object UniverseAPI extends XenocosmAPI {
  import xenocosm.json.hal.universe._

  val service = HttpService[IO] {
    case GET -> Root / ♠(universe) ⇒
      val range = Parsecs(10000)
      val response = UniverseResponse(universe, Point3.zero, range)
      Ok(response.asJson, jsonHal)
  }
}
