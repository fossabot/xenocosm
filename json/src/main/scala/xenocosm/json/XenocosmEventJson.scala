package xenocosm.json

import io.circe._
import spire.math.UInt

import xenocosm.{ShipMoved, TraderCreated, TraderSelected, XenocosmEvent}
import xenocosm.data.{ElapsedTime, Ship, Trader}

trait XenocosmEventJson {
  import io.circe.syntax._
  import interop.spire.json.instances._
  import elapsedTime._
  import ship._
  import trader._

  implicit val xenocosmEventHasJsonEncoder:Encoder[XenocosmEvent] =
    Encoder.instance({
      case ShipMoved(moves, ship, elapsed) => Json.obj(
        "event" -> "ship-moved".asJson,
        "moves" -> moves.asJson,
        "ship" -> ship.asJson,
        "elapsed" -> elapsed.asJson
      )

      case TraderCreated(moves, trader) => Json.obj(
        "event" -> "trader-created".asJson,
        "moves" -> moves.asJson,
        "trader" -> trader.asJson
      )

      case TraderSelected(moves, trader) => Json.obj(
        "event" -> "trader-selected".asJson,
        "moves" -> moves.asJson,
        "trader" -> trader.asJson
      )
    })

  implicit val xenocosmEventHasJsonDecoder:Decoder[XenocosmEvent] =
    Decoder.instance { hcur =>
      hcur.downField("event").as[String].flatMap({
        case "ship-moved" =>
          for {
            moves <- hcur.downField("moves").as[UInt]
            ship <- hcur.downField("ship").as[Ship]
            elapsed <- hcur.downField("elapsed").as[ElapsedTime]
          } yield ShipMoved(moves, ship, elapsed)

        case "trader-created" =>
          for {
            moves <- hcur.downField("moves").as[UInt]
            trader <- hcur.downField("trader").as[Trader]
          } yield TraderCreated(moves, trader)

        case "trader-selected" =>
          for {
            moves <- hcur.downField("moves").as[UInt]
            trader <- hcur.downField("trader").as[Trader]
          } yield TraderSelected(moves, trader)

        case event =>
          Left(DecodingFailure.apply(s"unrecognized event type: $event", List.empty[CursorOp]))
      })
    }
}
