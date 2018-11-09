package pseudoglot.json

import cats.Show
import io.circe._
import pseudoglot.parser.PhonesParser
import pseudoglot.data.{Phones, Transcription}

trait TranscriptionJson {
  import Phones.instances._

  implicit val transcriptionHasJsonEncode:Encoder[Transcription] =
    Encoder.encodeMap[String, String]
        .contramap[Transcription](_.map({ case (ks, v) => implicitly[Show[Phones]].show(ks) -> v }))

  implicit val transcriptionHasJsonDecode:Decoder[Transcription] =
    Decoder.decodeMap[String, String].map(PhonesParser.parse)
}
