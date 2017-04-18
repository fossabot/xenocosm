package xenocosm
package phonology

import cats.syntax.show._
import cats.kernel.laws.OrderLaws
import spire.random.Dist

import data._

class PhonotacticRuleNotationSpec extends XenocosmSuite {
  import PhonotacticRule.instances._
  import PhonotacticRuleNotation.instances._
  import PhonotacticRuleNotation.syntax._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  implicit val dist1:Dist[PhonotacticRule] = PhonotacticRule.dist(pulmonics, vowels)
  implicit val dist2:Dist[PhonotacticRuleNotation] = PhonotacticRuleNotation.dist(pulmonics, vowels)

  checkAll("Eq[PhonotacticRuleNotation]", OrderLaws[PhonotacticRuleNotation].eqv)

  test("DiceNotation.Roll.isomorphism") {
    forAll { (a:PhonotacticRule) ⇒ a.show.parsePhonotactics should be(PhonotacticRuleNotationParsed(a)) }
  }

  test("can parse rule: V") {
    val rule:PhonotacticRule = AnyVowel
    rule.show.parsePhonotactics should be(PhonotacticRuleNotationParsed(rule))
  }

  test("can parse rule: CV") {
    val rule:PhonotacticRule = Concat(List(AnyPulmonic, AnyVowel))
    rule.show.parsePhonotactics should be(PhonotacticRuleNotationParsed(rule))
  }

  test("can parse rule: VC") {
    val rule:PhonotacticRule = Concat(List(AnyVowel, AnyPulmonic))
    rule.show.parsePhonotactics should be(PhonotacticRuleNotationParsed(rule))
  }

  test("can parse rule: CVC") {
    val rule:PhonotacticRule = Concat(List(AnyPulmonic, AnyVowel, AnyPulmonic))
    rule.show.parsePhonotactics should be(PhonotacticRuleNotationParsed(rule))
  }

  test("can parse rule: CV(p|t)") {
    val rule:PhonotacticRule = Concat(List(
      AnyPulmonic,
      AnyVowel,
      Choose(List(
        LiteralPulmonic(Pulmonic(Voiceless, Bilabial, Plosive)),
        LiteralPulmonic(Pulmonic(Voiceless, Dental, Plosive))
      ))
    ))
    rule.show.parsePhonotactics should be(PhonotacticRuleNotationParsed(rule))
  }

  test("can get an Option[PhonotacticRule] from a PhonotacticRuleNotation") {
    val rule:PhonotacticRule = AnyVowel
    rule.show.parsePhonotactics.toOption should be (Some(rule))
    PhonotacticRuleNotationFailed("boom").toOption should be (None)
  }

  test("can get an Either[String, PhonotacticRule] from a PhonotacticRuleNotation") {
    val rule:PhonotacticRule = AnyVowel
    rule.show.parsePhonotactics.toEither should be (Right(rule))
    PhonotacticRuleNotationFailed("boom").toEither should be (Left("boom"))
  }
}
