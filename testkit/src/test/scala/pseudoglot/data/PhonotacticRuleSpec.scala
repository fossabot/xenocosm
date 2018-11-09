package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class PhonotacticRuleSpec extends xenocosm.test.XenocosmFunSuite {
  import PhonotacticRule.instances._

  implicit val arb:Arbitrary[PhonotacticRule] = Arbitrary(gen.phonotacticRule)

  checkAll("Eq[PhonotacticRule]", EqTests[PhonotacticRule].eqv)

  test("oddball rules") {
    val literalNull:PhonotacticRule = Literal(NullPhoneme)
    PhonotacticRule.toNotation(literalNull) shouldBe ""
  }
}
