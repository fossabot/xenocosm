package pseudoglot
package data

import cats.kernel.laws.discipline.{EqTests, MonoidTests}
import spire.random.Dist

class PhonotacticRuleSpec extends xenocosm.test.XenocosmSuite {
  import PhonotacticRule.instances._

  private val pulmonics = IPA.pulmonics.keys.toList
  private val vowels = IPA.vowels.keys.toList
  implicit private val dist:Dist[PhonotacticRule] =
    PhonotacticRule.ruleFromPhones(pulmonics ++ vowels)

  checkAll("Eq[PhonotacticRule]", EqTests[PhonotacticRule].eqv)
  checkAll("Monoid[PhonotacticRule]", MonoidTests[PhonotacticRule].monoid)

  test("oddball rules") {
    val emptyConcat:PhonotacticRule = Concat(List.empty[PhonotacticRule])
    PhonotacticRule.toNotation(emptyConcat) shouldBe ""

    val emptyChoose:PhonotacticRule = Choose(List.empty[PhonotacticRule])
    PhonotacticRule.toNotation(emptyChoose) shouldBe ""

    val singleConcat:PhonotacticRule = Concat(List(AnyVowel))
    PhonotacticRule.toNotation(singleConcat) shouldBe "V"

    val singleChoose:PhonotacticRule = Choose(List(AnyVowel))
    PhonotacticRule.toNotation(singleChoose) shouldBe "V"

    val literalNull:PhonotacticRule = Literal(NullPhoneme)
    PhonotacticRule.toNotation(literalNull) shouldBe ""
  }
}
