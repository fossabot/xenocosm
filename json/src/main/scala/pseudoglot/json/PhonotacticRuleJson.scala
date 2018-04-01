package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.parser.PhonotacticParser
import pseudoglot.data._

trait PhonotacticRuleJson {
  import PhonotacticRule.instances._

  implicit val phonotacticRuleHasJsonEncode:Encoder[PhonotacticRule] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val phonotacticRuleHasJsonDecode:Decoder[PhonotacticRule] =
    Decoder.decodeString.emap(PhonotacticParser.parse)
}
