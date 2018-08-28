package xenocosm.json

import io.circe._
import squants.space.{Length, Volume}

import xenocosm._

trait XenocosmErrorJson {
  import io.circe.syntax._
  import interop.squants.json.instances._

  implicit val xenocosmErrorHasJsonEncoder:Encoder[XenocosmError] =
    Encoder.instance({
      case NoMovesRemaining =>
        Json.obj("message" -> "no-moves-remaining".asJson)

      case NoTraderSelected =>
        Json.obj("message" -> "no-trader-selected".asJson)

      case CannotNavigate(maxNavDistance) =>
        Json.obj(
          "message" -> "cannot-navigate".asJson,
          "maxNavDistance" -> maxNavDistance.asJson
        )

      case NotEnoughFuel(unusedFuel) =>
        Json.obj(
          "message" -> "not-enough-fuel".asJson,
          "unusedFuel" -> unusedFuel.asJson
        )
    })

  implicit val xenocosmErrorHasJsonDecoder:Decoder[XenocosmError] =
    Decoder.instance { hcur =>
      hcur.downField("message").as[String].flatMap({
        case "no-moves-remaining" =>
          Right(NoMovesRemaining)

        case "no-trader-selected" =>
          Right(NoTraderSelected)

        case "cannot-navigate" =>
          hcur.downField("maxNavDistance").as[Length].map(CannotNavigate.apply)

        case "not-enough-fuel" =>
          hcur.downField("unusedFuel").as[Volume].map(NotEnoughFuel.apply)

        case message =>
          Left(DecodingFailure.apply(s"unrecognized error type: $message", List.empty[CursorOp]))
      })
    }
}
