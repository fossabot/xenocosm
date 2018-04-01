package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.data.Roundedness

trait RoundednessJson {
  import Roundedness.instances._

  implicit val roundednessHasJsonEncode:Encoder[Roundedness] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val roundednessHasJsonDecode:Decoder[Roundedness] =
    Decoder.decodeString.emap(Roundedness.parse)
}
