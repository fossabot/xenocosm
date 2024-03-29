package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.energy.Power

trait PowerJson {
  implicit val powerHasJsonEncode:Encoder[Power] =
    Encoder.encodeString.contramap[Power](_.toString())

  implicit val powerHasJsonDecode:Decoder[Power] =
    Decoder.decodeString.emapTry(Power.apply)
}
