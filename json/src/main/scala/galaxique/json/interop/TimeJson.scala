package galaxique.json
package interop

import io.circe._
import squants.time.Time

trait TimeJson {
  implicit val timeHasJsonEncode:Encoder[Time] =
    Encoder.encodeString.contramap[Time](_.toString())

  implicit val timeHasJsonDecode:Decoder[Time] =
    Decoder.decodeString.emapTry(Time.apply)
}
