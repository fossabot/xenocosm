package xenocosm
package phonology

import cats.Eq
import fastparse.all._
import data._
import spire.random.Dist

sealed trait PhonotacticRuleNotation extends Product with Serializable {
  def toOption:Option[PhonotacticRule]
  def toEither:Either[String, PhonotacticRule]
}

final case class PhonotacticRuleNotationFailed(msg:String) extends PhonotacticRuleNotation {
  def toOption:Option[PhonotacticRule] = None
  def toEither:Either[String, PhonotacticRule] = Left(msg)
}

final case class PhonotacticRuleNotationParsed(rule:PhonotacticRule) extends PhonotacticRuleNotation {
  def toOption:Option[PhonotacticRule] = Some(rule)
  def toEither:Either[String, PhonotacticRule] = Right(rule)
}

object PhonotacticRuleNotation {
  private val lookupPulmonic = (str:String) ⇒ IPA.pulmonics.map({ case (phone, xs) ⇒ (xs, phone)}).apply(str)
  private val lookupVowel = (str:String) ⇒ IPA.vowels.map({ case (phone, xs) ⇒ (xs, phone)}).apply(str)

  private lazy val anyPulmonic:Parser[AnyPulmonic.type] = P("C").map(_ ⇒ AnyPulmonic)

  private lazy val anyVowel:Parser[AnyVowel.type] = P("V").map(_ ⇒ AnyVowel)

  private lazy val literalPulmonic:Parser[LiteralPulmonic] =
    P(StringIn(IPA.pulmonics.values.toVector:_*).!).
      map(p ⇒ LiteralPulmonic(lookupPulmonic(p)))

  private lazy val literalVowel:Parser[LiteralVowel] =
    P(StringIn(IPA.vowels.values.toVector:_*).!).
      map(v ⇒ LiteralVowel(lookupVowel(v)))

  private lazy val single:Parser[PhonotacticRule] = P(anyPulmonic | anyVowel | literalPulmonic | literalVowel)

  private lazy val concat:Parser[Concat] =
    P((single | choose).rep).map(xs ⇒ Concat(xs.toList))

  private lazy val choose:Parser[PhonotacticRule] =
    P("(" ~ rules ~ ("|" ~ rules).rep.? ~ ")").map({
      case (x, None) ⇒ x
      case (x, Some(xs)) ⇒ Choose((xs :+ x).toList)
    })

  private lazy val rules:Parser[PhonotacticRule] = P(concat | single | choose)

  def parse(in:String):PhonotacticRuleNotation =
    rules.parse(in) match {
      case Parsed.Success(x, _) ⇒ PhonotacticRuleNotationParsed(PhonotacticRule.simplify(x))
      case failure:Parsed.Failure ⇒ PhonotacticRuleNotationFailed(failure.msg)
    }

  def dist(pulmomics:Seq[Pulmonic], vowels:Seq[Vowel]):Dist[PhonotacticRuleNotation] =
    PhonotacticRule.dist(pulmomics, vowels).map(PhonotacticRuleNotationParsed.apply)

  trait Instances {
    implicit val phonotacticRuleNotationHasEq:Eq[PhonotacticRuleNotation] =
      Eq.fromUniversalEquals[PhonotacticRuleNotation]
  }
  object instances extends Instances

  trait Syntax {
    implicit class StringOps(underlying:String) {
      def parsePhonotactics:PhonotacticRuleNotation =
        PhonotacticRuleNotation.parse(underlying)
    }
  }
  object syntax extends Syntax
}
