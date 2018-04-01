package galaxique.json
package interop

import io.circe._
import squants.space.Length

trait LengthJson {
  implicit val lengthHasJsonEncode:Encoder[Length] =
    Encoder.encodeString.contramap[Length](_.toString())

  implicit val lengthHasJsonDecode:Decoder[Length] =
    Decoder.decodeString.emapTry(Length.apply)
}
