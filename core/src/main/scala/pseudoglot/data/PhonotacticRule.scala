package pseudoglot
package data

import scala.annotation.tailrec
import cats.{Eq, Monoid, Show}
import cats.implicits._
import spire.random.Dist

sealed trait PhonotacticRule extends Product with Serializable
case object Empty extends PhonotacticRule
case object AnyPulmonic extends PhonotacticRule
case object AnyVowel extends PhonotacticRule
final case class Literal[T <: Phone](t:T) extends PhonotacticRule
final case class Choose(xs:List[PhonotacticRule]) extends PhonotacticRule
final case class Concat(xs:List[PhonotacticRule]) extends PhonotacticRule

object PhonotacticRule {
  import Phone.instances._
  import Phones.syntax._

  val ruleHasVowel:PhonotacticRule => Boolean = {
      case AnyVowel => true
      case Literal(_:Vowel) => true
      case Concat(Nil) => false
      case Concat(x :: xs) => ruleHasVowel(x) || ruleHasVowel(Concat(xs))
      case Choose(Nil) => false
      case Choose(x :: xs) => ruleHasVowel(x) && ruleHasVowel(Choose(xs))
      case _ => false
    }

  val startsWithVowel:PhonotacticRule => Boolean = {
      case AnyVowel => true
      case Literal(_:Vowel) => true
      case Concat(Nil) => false
      case Concat(x :: _) => startsWithVowel(x)
      case Choose(Nil) => false
      case Choose(x :: xs) => startsWithVowel(x) && startsWithVowel(Choose(xs))
      case _ => false
    }

  private def toNotation(sep:String, rules:List[PhonotacticRule]):String =
    rules.filter(_ != Empty) match {
      case Nil => ""
      case x :: Nil => toNotation(x)
      case xs => s"(${xs.foldRight(List.empty[String])(toNotation(_) :: _).mkString(sep)})"
    }

  val toNotation:PhonotacticRule => String = {
      case Empty ⇒ ""
      case AnyPulmonic ⇒ "P"
      case AnyVowel ⇒ "V"
      case Literal(NullPhoneme) ⇒ ""
      case Literal(p:Pulmonic) ⇒ s"[${p.show}]"
      case Literal(v:Vowel) ⇒ s"[${v.show}]"
      case Concat(rules) ⇒ toNotation("", rules)
      case Choose(rules) ⇒ toNotation("|", rules)
    }

  val literalPulmonic:Phones => Dist[PhonotacticRule] =
    phones => Dist.oneOf(phones.pulmonics:_*).map(Literal.apply)

  val literalVowel:Phones => Dist[PhonotacticRule] =
    phones => Dist.oneOf(phones.vowels:_*).map(Literal.apply)

  // scalastyle:off cyclomatic.complexity
  val ruleFromPhones:Phones => Dist[PhonotacticRule] = phones => Dist.gen { gen ⇒
    val pulmonicDist = literalPulmonic(phones)
    val vowelDist = literalVowel(phones)

    @tailrec
    def loop(acc:PhonotacticRule):PhonotacticRule = (acc, gen.nextDouble()) match {
      case (AnyVowel, d)            if d > PROB_LITERAL ⇒ loop(vowelDist(gen))
      case (AnyPulmonic, d)         if d > PROB_LITERAL ⇒ loop(pulmonicDist(gen))
      case (Literal(x:Vowel), d)    if d > PROB_CONCAT ⇒ loop(Concat(List(AnyPulmonic, Literal(x))))
      case (Literal(x:Pulmonic), d) if d > PROB_CONCAT ⇒ loop(Concat(List(AnyVowel, Literal(x))))
      case (Concat(xs), d)          if d > PROB_CHOOSE ⇒ Choose(xs)
      case _ ⇒ acc
    }

    loop(gen.oneOf(AnyPulmonic, AnyVowel))
  }
  // scalastyle:on cyclomatic.complexity

  trait Instances {
    implicit val phonotacticRuleHasEq:Eq[PhonotacticRule] =
      Eq.fromUniversalEquals[PhonotacticRule]

    implicit val phonotacticRuleHasMonoid:Monoid[PhonotacticRule] =
      new Monoid[PhonotacticRule] {
        def empty:PhonotacticRule = Empty
        def combine(x:PhonotacticRule, y:PhonotacticRule): PhonotacticRule =
          (x, y) match {
            case (Empty, b) => b
            case (a, Empty) => a
            case (Concat(as), Concat(bs)) => Concat(as ++ bs)
            case (a, Concat(bs)) => Concat(a :: bs)
            case (Concat(as), b) => Concat(as :+ b)
            case _ => Concat(List(x, y))
          }
      }

    implicit val phonotacticRuleHasShow:Show[PhonotacticRule] =
      Show.show[PhonotacticRule](toNotation)
  }
  object instances extends Instances

  trait Syntax {
    implicit class PhonotacticRuleOps(underlying:PhonotacticRule) {
      val hasVowel:Boolean = PhonotacticRule.ruleHasVowel(underlying)
      val startsWithVowel:Boolean = PhonotacticRule.startsWithVowel(underlying)
    }
  }
  object syntax extends Syntax
}
