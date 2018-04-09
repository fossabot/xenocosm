package xenocosm.http
package service

import cats.effect.IO
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.headers.Location

import xenocosm.http.data.ApiResponse

object Home extends XenocosmAPI {
  import ApiResponse.instances._

  val service = HttpService[IO] {
    case GET -> Root ⇒
      SeeOther(Location(uri("/v1")))
    case GET -> Root / "v1" ⇒
      Ok(ApiResponse(1).asJson, jsonHal)
  }
}
