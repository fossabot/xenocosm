package xenocosm.json

import io.circe._

import xenocosm.{ShipMoved, TraderCreated, TraderSelected, XenocosmEvent}
import xenocosm.data.{CosmicLocation, Trader}

trait XenocosmEventJson {
  import io.circe.syntax._
  import cosmicLocation._
  import trader._

  implicit val xenocosmEventHasJsonEncoder:Encoder[XenocosmEvent] =
    Encoder.instance({
      case ShipMoved(loc) => Json.obj(
        "event" -> "ship-moved".asJson,
        "loc" -> loc.asJson
      )

      case TraderCreated(trader) => Json.obj(
        "event" -> "trader-created".asJson,
        "trader" -> trader.asJson
      )

      case TraderSelected(trader) => Json.obj(
        "event" -> "trader-selected".asJson,
        "trader" -> trader.asJson
      )
    })

  implicit val xenocosmEventHasJsonDecoder:Decoder[XenocosmEvent] =
    Decoder.instance { hcur =>
      hcur.downField("event").as[String].flatMap({
        case "ship-moved" =>
          for {
            loc <- hcur.downField("loc").as[CosmicLocation]
          } yield ShipMoved(loc)

        case "trader-created" =>
          for {
            trader <- hcur.downField("trader").as[Trader]
          } yield TraderCreated(trader)

        case "trader-selected" =>
          for {
            trader <- hcur.downField("trader").as[Trader]
          } yield TraderSelected(trader)

        case event =>
          Left(DecodingFailure.apply(s"unrecognized event type: $event", List.empty[CursorOp]))
      })
    }
}
