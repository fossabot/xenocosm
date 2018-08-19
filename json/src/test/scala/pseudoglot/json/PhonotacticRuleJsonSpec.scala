package pseudoglot.json

import io.circe.syntax._
import spire.random.Dist

import pseudoglot.IPA
import pseudoglot.data.PhonotacticRule

class PhonotacticRuleJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import phonotacticRule._

  implicit private val dist:Dist[PhonotacticRule] =
    PhonotacticRule.ruleFromPhones((IPA.pulmonics ++ IPA.vowels).keys.toList)

  test("PhonotacticRule.json.isomorphism") {
    forAll { a:PhonotacticRule =>
      a.asJson.as[PhonotacticRule] shouldBe Right(a)
    }
  }
}
