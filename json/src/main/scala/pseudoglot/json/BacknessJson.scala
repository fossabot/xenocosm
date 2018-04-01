package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.data.Backness

trait BacknessJson {
  import Backness.instances._

  implicit val backnessHasJsonEncode:Encoder[Backness] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val backnessHasJsonDecode:Decoder[Backness] =
    Decoder.decodeString.emap(Backness.parse)
}
