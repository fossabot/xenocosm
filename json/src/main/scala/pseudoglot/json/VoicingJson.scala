package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.data.Voicing

trait VoicingJson {
  import Voicing.instances._

  implicit val voicingHasJsonEncode:Encoder[Voicing] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val voicingHasJsonDecode:Decoder[Voicing] =
    Decoder.decodeString.emap(Voicing.parse)
}
