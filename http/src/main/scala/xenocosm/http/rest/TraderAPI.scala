package xenocosm.http
package rest

import cats.effect.IO
import io.circe._
import io.circe.syntax._
import org.http4s.{AuthedService, EntityDecoder}
import org.http4s.circe._
import org.http4s.dsl.io._
import spire.random.Generator

import xenocosm.{CreateTrader, NoMovesRemaining, TooFar, TraderCreated}
import xenocosm.data.Identity
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.response.{ErrorResponse, TraderResponse}
import xenocosm.http.services.DataStore

final class TraderAPI(val auth:XenocosmAuthentication, val data:DataStore, val gen:Generator) {
  import xenocosm.CommandHandler.syntax._
  import xenocosm.XenocosmCommand.instances._
  import xenocosm.json.command.instances._
  import ErrorResponse.instances._
  import TraderResponse.instances._

  private implicit val decoder:EntityDecoder[IO, CreateTrader] = jsonOf[IO, CreateTrader]

  val service:AuthedService[Identity, IO] = AuthedService[Identity, IO] {
    case req @ POST -> Root as identity ⇒
      req.req.as[CreateTrader].flatMap( _.verify.value(gen) match {
        case Right(TraderCreated(moves, trader)) =>
          Created(TraderResponse(trader).asJson, jsonHal)
        case Left(err:NoMovesRemaining.type) =>
          Forbidden(ErrorResponse(err).asJson, jsonHal)
        case Left(err:TooFar) =>
          Forbidden(ErrorResponse(err).asJson, jsonHal)
        case Left(_) =>
          InternalServerError()
      })

    case GET -> Root / UuidSegment(_) as identity ⇒
      Ok(Json.Null, jsonHal)
  }
}
