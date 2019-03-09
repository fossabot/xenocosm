package pseudoglot
package data

import cats.Eq
import spire.random.{Dist, Generator}

sealed trait OnomasticRule { self =>
  def apply(morphology: Morphology):Dist[List[Phones]] =
    Dist.gen(gen => OnomasticRule.applyRule(morphology, self)(gen))
}
case object Mononym extends OnomasticRule
final case class Polynym(lhs: OnomasticRule, rhs: OnomasticRule) extends OnomasticRule

object OnomasticRule {

  private def elaborateRule(current: OnomasticRule)(gen: Generator): OnomasticRule =
    if (gen.nextBoolean()) {
      current match {
        case Mononym =>
          elaborateRule(Polynym(Mononym, Mononym))(gen)
        case Polynym(lhs, rhs) =>
          elaborateRule(Polynym(lhs, Polynym(Mononym, rhs)))(gen)
      }
    } else {
      current
    }

  def dist:Dist[OnomasticRule] = Dist.gen(elaborateRule(Mononym))

  private def applyRule(morphology: Morphology, rule:OnomasticRule)(gen:Generator):List[Phones] =
    rule match {
      case Mononym ⇒ List(morphology.morpheme(gen))
      case Polynym(lhs, rhs) ⇒ applyRule(morphology, lhs)(gen) ++ applyRule(morphology, rhs)(gen)
    }

  trait Instances {
    implicit val onomasticRuleHasEq:Eq[OnomasticRule] =
      Eq.fromUniversalEquals[OnomasticRule]
  }
  object instances extends Instances
}
