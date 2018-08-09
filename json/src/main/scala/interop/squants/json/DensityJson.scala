package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.mass.Density

trait DensityJson {
  implicit val densityHasJsonEncode:Encoder[Density] =
    Encoder.encodeString.contramap[Density](_.toString())

  implicit val densityHasJsonDecode:Decoder[Density] =
    Decoder.decodeString.emapTry(Density.apply)
}
