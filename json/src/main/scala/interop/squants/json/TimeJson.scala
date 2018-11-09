package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.time.Time

trait TimeJson {
  implicit val timeHasJsonEncode:Encoder[Time] =
    Encoder.encodeString.contramap[Time](_.toString())

  implicit val timeHasJsonDecode:Decoder[Time] =
    Decoder.decodeString.emapTry(Time.apply)
}
