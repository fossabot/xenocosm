package pseudoglot
package data

import cats.{Eq, Show}
import cats.implicits._
import spire.random.{Dist, Generator}

sealed trait PhonotacticRule extends Product with Serializable
case object Empty extends PhonotacticRule
case object AnyPulmonic extends PhonotacticRule
case object AnyVowel extends PhonotacticRule
final case class Literal[T <: Phone](t:T) extends PhonotacticRule
final case class Choose(lhs:PhonotacticRule, rhs:PhonotacticRule) extends PhonotacticRule
final case class Concat(lhs:PhonotacticRule, rhs:PhonotacticRule) extends PhonotacticRule

object PhonotacticRule {
  import Phone.instances._

  val root:PhonotacticRule = Concat(AnyPulmonic, Concat(AnyVowel, AnyPulmonic))

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

  val minimize:PhonotacticRule => PhonotacticRule = {
    case Choose(Empty, rule) => minimize(rule)
    case Choose(rule, Empty) => minimize(rule)
    case Concat(Empty, rule) => minimize(rule)
    case Concat(rule, Empty) => minimize(rule)
    case rule => rule
  }

  private def elaborateRule(gen: Generator, pulmonics: List[Pulmonic], vowels: List[Vowel])(model: PhonotacticRule): PhonotacticRule = {
    val f = elaborateRule(gen, pulmonics, vowels)(_)
    val p = gen.nextBoolean()
    model match {
      case AnyPulmonic if p => Concat(Literal(gen.chooseFromSeq(pulmonics)), Literal(gen.chooseFromSeq(pulmonics)))
      case AnyVowel if p => Concat(Literal(gen.chooseFromSeq(vowels)), Literal(gen.chooseFromSeq(vowels)))
      case Concat(lhs, rhs) => Concat(f(lhs), f(rhs))
      case Choose(lhs, rhs) => Choose(f(lhs), f(rhs))
      case _ => model
    }
  }

  private def elaborateRules(gen: Generator, pulmonics: List[Pulmonic], vowels: List[Vowel])(current: Vector[PhonotacticRule]): Vector[PhonotacticRule] = {
    val f = elaborateRule(gen, pulmonics, vowels)(_)
    val g = elaborateRules(gen, pulmonics, vowels)(_)

    if (gen.nextBoolean()) {
      g(f(current.head) +: current :+ f(current.last))
    } else {
      current
    }
  }

  def rulesFromPhones(pulmonics:List[Pulmonic], vowels:List[Vowel]):Dist[List[PhonotacticRule]] =
    Dist.gen(elaborateRules(_, pulmonics, vowels)(Vector(root)).distinct.toList)

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
