package pseudoglot
package data

import scala.annotation.tailrec
import cats.{Eq, Show}
import cats.data.NonEmptyList
import cats.implicits._
import spire.random.Dist

sealed trait PhonotacticRule extends Product with Serializable
case object Empty extends PhonotacticRule
case object AnyPulmonic extends PhonotacticRule
case object AnyVowel extends PhonotacticRule
final case class Literal[T <: Phone](t:T) extends PhonotacticRule
final case class Choose(lhs:PhonotacticRule, rhs:PhonotacticRule) extends PhonotacticRule
final case class Concat(lhs:PhonotacticRule, rhs:PhonotacticRule) extends PhonotacticRule

object PhonotacticRule {
  import Phone.instances._
  import Phones.syntax._

  val ruleHasVowel:PhonotacticRule => Boolean = {
      case AnyVowel => true
      case Literal(_:Vowel) => true
      case Concat(lhs, rhs) => ruleHasVowel(lhs) || ruleHasVowel(rhs)
      case Choose(lhs, rhs) => ruleHasVowel(lhs) && ruleHasVowel(rhs)
      case _ => false
    }

  val startsWithVowel:PhonotacticRule => Boolean = {
      case AnyVowel => true
      case Literal(_:Vowel) => true
      case Concat(lhs, _) => startsWithVowel(lhs)
      case Choose(lhs, rhs) => startsWithVowel(lhs) && startsWithVowel(rhs)
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
      case Concat(lhs, rhs) ⇒ toNotation("", List(lhs, rhs))
      case Choose(lhs, rhs) ⇒ toNotation("|", List(lhs, rhs))
    }

  val literalPulmonic:Phones => Dist[PhonotacticRule] =
    phones => Dist.oneOf(phones.pulmonics:_*).map(Literal.apply)

  val literalVowel:Phones => Dist[PhonotacticRule] =
    phones => Dist.oneOf(phones.vowels:_*).map(Literal.apply)

  val minimize:PhonotacticRule => PhonotacticRule = {
    case Choose(Empty, rule) => minimize(rule)
    case Choose(rule, Empty) => minimize(rule)
    case Concat(Empty, rule) => minimize(rule)
    case Concat(rule, Empty) => minimize(rule)
    case rule => rule
  }

  // scalastyle:off cyclomatic.complexity
  def ruleFromPhones(phones:Phones)(implicit magic:Magic):Dist[PhonotacticRule] = Dist.gen { gen ⇒
    val pulmonicDist = literalPulmonic(phones)
    val vowelDist = literalVowel(phones)

    @tailrec
    def loop(acc:PhonotacticRule):PhonotacticRule = (acc, gen.nextDouble()) match {
      case (AnyVowel, d)            if d > magic.literal ⇒ loop(vowelDist(gen))
      case (AnyPulmonic, d)         if d > magic.literal ⇒ loop(pulmonicDist(gen))
      case (Literal(x:Vowel), d)    if d > magic.concat ⇒ loop(Concat(AnyPulmonic, Literal(x)))
      case (Literal(x:Pulmonic), d) if d > magic.concat ⇒ loop(Concat(AnyVowel, Literal(x)))
      case (Concat(lhs, rhs), d)    if d > magic.choose ⇒ Choose(lhs, rhs)
      case _ ⇒ acc
    }

    minimize(loop(gen.oneOf(AnyPulmonic, AnyVowel)))
  }
  // scalastyle:on cyclomatic.complexity

  def ruleFromPhones(phones:List[Phone])(implicit magic:Magic):Dist[PhonotacticRule] =
    phones match {
      case Nil => Dist.constant(AnyVowel)
      case x :: xs => ruleFromPhones(NonEmptyList(x, xs))
    }

  trait Instances {
    implicit val phonotacticRuleHasEq:Eq[PhonotacticRule] =
      Eq.fromUniversalEquals[PhonotacticRule]

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
