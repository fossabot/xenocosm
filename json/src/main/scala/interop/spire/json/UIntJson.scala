package interop.spire.json

import io.circe.{Decoder, Encoder}
import spire.math.UInt

trait UIntJson {
  implicit val uintHasJsonEncode:Encoder[UInt] =
    Encoder.encodeInt.contramap[UInt](_.toInt)

  implicit val uintHasJsonDecode:Decoder[UInt] =
    Decoder.decodeInt.map(UInt.apply)
}
