package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.data.Manner

trait MannerJson {
  import Manner.instances._

  implicit val mannerHasJsonEncode:Encoder[Manner] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val mannerHasJsonDecode:Decoder[Manner] =
    Decoder.decodeString.emap(Manner.parse)
}
