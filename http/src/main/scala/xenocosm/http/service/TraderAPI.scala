package xenocosm.http
package service

import cats.effect.IO
import io.circe._
import io.circe.syntax._
import org.http4s.{EntityDecoder, HttpService}
import org.http4s.circe._
import org.http4s.dsl.io._
import spire.random.Generator

import xenocosm.{CreateTrader, NoMovesRemaining, TooFar, TraderCreated}

object TraderAPI {
  import xenocosm.CommandHandler.syntax._
  import xenocosm.XenocosmCommand.instances._
  import xenocosm.json.command.instances._
  import xenocosm.json.trader._
  import xenocosm.http.response.ErrorInstances.instances._

  private implicit val decoder:EntityDecoder[IO, CreateTrader] = jsonOf[IO, CreateTrader]

  val service:Generator => HttpService[IO] = gen => HttpService[IO] {
    case req @ POST -> Root / "v1" / "trader" ⇒
      req.as[CreateTrader].flatMap( _.verify.value(gen) match {
        case Right(TraderCreated(moves, trader)) =>
          Created(trader.asJson, jsonHal)
        case Left(err:NoMovesRemaining.type) =>
          Forbidden(err.asJson, jsonHal)
        case Left(err:TooFar) =>
          Forbidden(err.asJson, jsonHal)
        case Left(_) =>
          InternalServerError()
      })

    case GET -> Root / "v1" / "trader" ⇒
      Ok(Json.Null, jsonHal)
  }
}
