package xenocosm.http
package service

import cats.effect.IO
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.io._

import xenocosm.http.data.MultiverseResponse

object MultiverseAPI extends XenocosmAPI {
  import MultiverseResponse.instances._

  val service = HttpService[IO] {
    case GET -> Root ⇒
      Ok(MultiverseResponse.asJson, jsonHal)
  }
}
