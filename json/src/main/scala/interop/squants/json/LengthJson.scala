package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.space.Length

trait LengthJson {
  implicit val lengthHasJsonEncode:Encoder[Length] =
    Encoder.encodeString.contramap[Length](_.toString())

  implicit val lengthHasJsonDecode:Decoder[Length] =
    Decoder.decodeString.emapTry(Length.apply)
}
