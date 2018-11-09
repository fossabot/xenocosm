package pseudoglot
package parser

import cats.kernel.laws.discipline.EqTests
import cats.implicits._
import org.scalacheck.Arbitrary

class PhonotacticParserSpec extends xenocosm.test.XenocosmFunSuite {
  import pseudoglot.data._
  import PhonotacticRule.instances._
  import PhonotacticParser.instances._
  import PhonotacticParser.syntax._

  implicit val arb:Arbitrary[PhonotacticRule] = Arbitrary(gen.phonotacticRule)

  checkAll("Eq[PhonotacticParser]", EqTests[PhonotacticParser].eqv)

  test("PhonotacticRule.show.parse.isomorphism") {
    forAll { a:PhonotacticRule ⇒ a.show.asPhonotactics shouldBe Right(PhonotacticRule.minimize(a)) }
  }

  test("can parse empty rule") {
    val rule:PhonotacticRule = Empty
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: P") {
    val rule:PhonotacticRule = AnyPulmonic
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: V") {
    val rule:PhonotacticRule = AnyVowel
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: p") {
    val rule:PhonotacticRule = Literal[Pulmonic](Pulmonic(Voiceless, Bilabial, Plosive))
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: i") {
    val rule:PhonotacticRule = Literal[Vowel](Vowel(Unrounded, Close, Front))
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: PV") {
    val rule:PhonotacticRule = Concat(AnyPulmonic, AnyVowel)
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: VP") {
    val rule:PhonotacticRule = Concat(AnyVowel, AnyPulmonic)
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: PVP") {
    val rule:PhonotacticRule = Concat(AnyPulmonic, Concat(AnyVowel, AnyPulmonic))
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("can parse rule: PV(p|t)") {
    val rule:PhonotacticRule = Concat(AnyPulmonic, Concat(AnyVowel, Choose(
      Literal[Pulmonic](Pulmonic(Voiceless, Bilabial, Plosive)),
      Literal[Pulmonic](Pulmonic(Voiceless, Dental, Plosive))
    )))
    rule.show.asPhonotactics shouldBe Right(rule)
  }

  test("parse.failure") {
    PhonotacticParser.parse("{}") shouldBe Left("End:1:1 ...\"{}\"")
  }
}
