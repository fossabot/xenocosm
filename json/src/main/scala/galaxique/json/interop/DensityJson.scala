package galaxique.json
package interop

import io.circe._
import squants.mass.Density

trait DensityJson {
  implicit val densityHasJsonEncode:Encoder[Density] =
    Encoder.encodeString.contramap[Density](_.toString())

  implicit val densityHasJsonDecode:Decoder[Density] =
    Decoder.decodeString.emapTry(Density.apply)
}
