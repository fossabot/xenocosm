package galaxique.json
package interop

import io.circe._
import squants.mass.Mass

trait MassJson {
  implicit val massHasJsonEncode:Encoder[Mass] =
    Encoder.encodeString.contramap[Mass](_.toString())

  implicit val massHasJsonDecode:Decoder[Mass] =
    Decoder.decodeString.emapTry(Mass.apply)
}
