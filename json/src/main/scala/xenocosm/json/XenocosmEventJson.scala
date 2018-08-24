package xenocosm.json

import io.circe._
import spire.math.UInt
import squants.time.Time

import xenocosm.{ShipMoved, TraderCreated, XenocosmEvent}
import xenocosm.data.{Ship, Trader}

trait XenocosmEventJson {
  import io.circe.syntax._
  import interop.squants.json.instances._
  import interop.spire.json.instances._
  import ship._
  import trader._

  implicit val xenocosmEventHasJsonEncoder:Encoder[XenocosmEvent] =
    Encoder.instance({
      case ShipMoved(moves, ship, moving, stationary) => Json.obj(
        "event" -> "ship-moved".asJson,
        "moves" -> moves.asJson,
        "ship" -> ship.asJson,
        "moving" -> moving.asJson,
        "stationary" -> stationary.asJson
      )

      case TraderCreated(moves, trader) => Json.obj(
        "event" -> "too-far".asJson,
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
            moving <- hcur.downField("moving").as[Time]
            stationary <- hcur.downField("stationary").as[Time]
          } yield ShipMoved(moves, ship, moving, stationary)

        case "too-far" =>
          for {
            moves <- hcur.downField("moves").as[UInt]
            trader <- hcur.downField("trader").as[Trader]
          } yield TraderCreated(moves, trader)

        case event =>
          Left(DecodingFailure.apply(s"unrecognized event type: $event", List.empty[CursorOp]))
      })
    }
}
