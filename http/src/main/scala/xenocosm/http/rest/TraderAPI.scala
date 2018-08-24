package xenocosm.http
package rest

import cats.effect.IO
import io.circe._
import org.http4s.AuthedService
import org.http4s.circe._
import org.http4s.dsl.io._
import spire.random.Generator

import xenocosm.data.Identity
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.DataStore

final class TraderAPI(val auth:XenocosmAuthentication, val data:DataStore, val gen:Generator) {

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {
    case req @ POST -> Root as identity ⇒ ???

    case GET -> Root / UuidSegment(_) as identity ⇒
      Ok(Json.Null, jsonHal)
  }
}
