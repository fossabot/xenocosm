package pseudoglot.json

import io.circe._
import io.circe.syntax._
import pseudoglot.data._

trait PulmonicJson {
  import manner._
  import place._
  import voicing._

  implicit val pulmonicHasJsonEncode:Encoder[Pulmonic] =
    Encoder.instance {
      pulmonic => Json.arr(
        pulmonic.voicing.asJson,
        pulmonic.place.asJson,
        pulmonic.manner.asJson
      )
    }

  implicit val pulmonicHasJsonDecode:Decoder[Pulmonic] =
    Decoder.instance { hcur =>
      for {
        voicing <- hcur.downN(0).as[Voicing]
        place <- hcur.downN(1).as[Place]
        manner <- hcur.downN(2).as[Manner]
      } yield Pulmonic(voicing, place, manner)
    }
}
