package interop.spire.json

import io.circe.{Decoder, Encoder}
import spire.math.UInt

trait UIntJson {
  implicit val densityHasJsonEncode:Encoder[UInt] =
    Encoder.encodeInt.contramap[UInt](_.toInt)

  implicit val densityHasJsonDecode:Decoder[UInt] =
    Decoder.decodeInt.map(UInt.apply)
}
