package xenocosm.json

import io.circe._
import squants.space.Length

import xenocosm.{NoMovesRemaining, TooFar, XenocosmError}

trait XenocosmErrorJson {
  import io.circe.syntax._
  import interop.squants.json.instances._

  implicit val xenocosmErrorHasJsonEncoder:Encoder[XenocosmError] =
    Encoder.instance({
      case NoMovesRemaining =>
        Json.obj("message" -> "no-moves-remaining".asJson)

      case TooFar(distance) =>
        Json.obj(
          "message" -> "too-far".asJson,
          "distance" -> distance.asJson
        )
    })

  implicit val xenocosmErrorHasJsonDecoder:Decoder[XenocosmError] =
    Decoder.instance { hcur =>
      hcur.downField("message").as[String].flatMap({
        case "no-moves-remaining" =>
          Right(NoMovesRemaining)

        case "too-far" =>
          hcur.downField("distance").as[Length].map(TooFar.apply)

        case message =>
          Left(DecodingFailure.apply(s"unrecognized error type: $message", List.empty[CursorOp]))
      })
    }
}
