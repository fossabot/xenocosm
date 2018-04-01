package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.data.Height

trait HeightJson {
  import Height.instances._

  implicit val heightHasJsonEncode:Encoder[Height] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val heightHasJsonDecode:Decoder[Height] =
    Decoder.decodeString.emap(Height.parse)
}
