package galaxique.json
package interop

import io.circe._
import squants.space.Volume

trait VolumeJson {
  implicit val volumeHasJsonEncode:Encoder[Volume] =
    Encoder.encodeString.contramap[Volume](_.toString())

  implicit val volumeHasJsonDecode:Decoder[Volume] =
    Decoder.decodeString.emapTry(Volume.apply)
}
