package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.parser.PhonesParser
import pseudoglot.data.{Phones, Transcription}

trait TranscriptionJson {
  import Phones.instances._

  implicit val transcriptionHasJsonEncode:Encoder[Transcription] =
    Encoder.encodeMap[String, String]
        .contramap[Transcription](_.map({ case (ks, v) => ks.show -> v }))

  implicit val transcriptionHasJsonDecode:Decoder[Transcription] =
    Decoder.decodeMap[String, String].map(PhonesParser.parse)
}
