package pseudoglot.json

import io.circe._
import io.circe.syntax._
import pseudoglot.data._

trait VowelJson {
  import backness._
  import height._
  import roundedness._

  implicit val vowelHasJsonEncode:Encoder[Vowel] =
    Encoder.instance {
      vowel => Json.arr(
        vowel.roundedness.asJson,
        vowel.height.asJson,
        vowel.backness.asJson
      )
    }

  implicit val vowelHasJsonDecode:Decoder[Vowel] =
    Decoder.instance { hcur =>
      for {
        roundedness <- hcur.downN(0).as[Roundedness]
        height <- hcur.downN(1).as[Height]
        backness <- hcur.downN(2).as[Backness]
      } yield Vowel(roundedness, height, backness)
    }
}
