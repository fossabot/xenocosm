package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import cats.implicits._
import spire.random.Dist

class PhonotacticRuleSpec extends xenocosm.test.XenocosmSuite {
  import PhoneSeq.syntax._
  import PhonotacticRule.instances._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  implicit private val dist:Dist[PhonotacticRule] =
    for {
      p <- pulmonics.distRule
      v <- vowels.distRule
    } yield p |+| v

  checkAll("Eq[PhonotacticRule]", EqTests[PhonotacticRule].eqv)

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
