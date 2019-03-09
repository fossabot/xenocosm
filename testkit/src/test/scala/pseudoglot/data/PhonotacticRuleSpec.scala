package pseudoglot
package data

import cats.data.NonEmptyList
import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.random.rng.BurtleRot2

class PhonotacticRuleSpec extends xenocosm.test.XenocosmFunSuite {
  import PhonotacticRule.instances._

  implicit val arb:Arbitrary[PhonotacticRule] = Arbitrary(gen.phonotacticRule)

  checkAll("Eq[PhonotacticRule]", EqTests[PhonotacticRule].eqv)

  test("PhonotacticRule.proceeds.from.Phones") {
    implicit val arbA:Arbitrary[NonEmptyList[Pulmonic]] = Arbitrary(gen.pulmonics)
    implicit val arbB:Arbitrary[NonEmptyList[Vowel]] = Arbitrary(gen.vowels)
    val seed:Array[Int] = genInts.sample.get
    forAll { (pulmonics:NonEmptyList[Pulmonic], vowels:NonEmptyList[Vowel]) =>
      val lhs = PhonotacticRule.rulesFromPhones(pulmonics.toList, vowels.toList)(BurtleRot2.fromSeed(seed))
      val rhs = PhonotacticRule.rulesFromPhones(pulmonics.toList, vowels.toList)(BurtleRot2.fromSeed(seed))

      lhs shouldBe rhs
    }
  }

  test("oddball rules") {
    val literalNull:PhonotacticRule = Literal(NullPhoneme)
    PhonotacticRule.toNotation(literalNull) shouldBe ""
  }
}
