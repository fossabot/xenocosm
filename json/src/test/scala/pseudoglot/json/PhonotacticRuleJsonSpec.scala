package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.PhonotacticRule

class PhonotacticRuleJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import phonotacticRule._

  implicit val arb:Arbitrary[PhonotacticRule] = Arbitrary(pseudoglot.gen.phonotacticRule)

  test("PhonotacticRule.json.isomorphism") {
    forAll { a:PhonotacticRule =>
      a.asJson.as[PhonotacticRule] shouldBe Right(PhonotacticRule.minimize(a))
    }
  }
}
