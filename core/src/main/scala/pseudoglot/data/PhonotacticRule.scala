package pseudoglot
package data

import cats.{Eq, Monoid, Show}
import cats.implicits._

sealed trait PhonotacticRule extends Product with Serializable
case object Empty extends PhonotacticRule
case object AnyPulmonic extends PhonotacticRule
case object AnyVowel extends PhonotacticRule
final case class Literal[T <: Phone](t:T) extends PhonotacticRule
final case class Choose(xs:List[PhonotacticRule]) extends PhonotacticRule
final case class Concat(xs:List[PhonotacticRule]) extends PhonotacticRule

object PhonotacticRule {
  import Phone.instances._

  private def ruleHasVowel(rule:PhonotacticRule):Boolean =
    rule match {
      case AnyVowel => true
      case Literal(_:Vowel) => true
      case Concat(Nil) => false
      case Concat(x :: xs) => ruleHasVowel(x) || ruleHasVowel(Concat(xs))
      case Choose(Nil) => false
      case Choose(x :: xs) => ruleHasVowel(x) && ruleHasVowel(Choose(xs))
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
      def hasVowel:Boolean = ruleHasVowel(underlying)
    }
  }
  object syntax extends Syntax
}
