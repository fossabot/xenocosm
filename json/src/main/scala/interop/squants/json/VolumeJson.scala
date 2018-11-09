package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.space.Volume

trait VolumeJson {
  implicit val volumeHasJsonEncode:Encoder[Volume] =
    Encoder.encodeString.contramap[Volume](_.toString())

  implicit val volumeHasJsonDecode:Decoder[Volume] =
    Decoder.decodeString.emapTry(Volume.apply)
}
