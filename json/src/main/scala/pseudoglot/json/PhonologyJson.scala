package pseudoglot.json

import io.circe._
import io.circe.syntax._
import pseudoglot.data._

trait PhonologyJson {
  import phonotacticRule._
  import pulmonic._
  import vowel._

  implicit val phonologyHasJsonEncode:Encoder[Phonology] =
    Encoder.instance {
      phonology => Json.obj(
        "pulmonics" -> phonology.pulmonics.asJson,
        "vowels" -> phonology.vowels.asJson,
        "phonotactics" -> phonology.phonotactics.asJson
      )
    }

  implicit val phonologyHasJsonDecode:Decoder[Phonology] =
    Decoder.instance { hcur =>
      for {
        pulmonics <- hcur.downField("pulmonics").as[List[Pulmonic]]
        vowels <- hcur.downField("vowels").as[List[Vowel]]
        phonotactics <- hcur.downField("phonotactics").as[PhonotacticRules]
      } yield Phonology(pulmonics, vowels, phonotactics)
    }
}
