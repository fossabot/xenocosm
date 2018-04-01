package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.parser.PhoneSeqParser
import pseudoglot.data.{PhoneSeq, Transcription}

trait TranscriptionJson {
  import PhoneSeq.instances._

  implicit val transcriptionHasJsonEncode:Encoder[Transcription] =
    Encoder.encodeMap[String, String]
        .contramap[Transcription](_.map({ case (ks, v) => ks.show -> v }))

  implicit val transcriptionHasJsonDecode:Decoder[Transcription] =
    Decoder.decodeMap[String, String].map(PhoneSeqParser.parse)
}
