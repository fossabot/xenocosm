package pseudoglot
package data

import cats.data.NonEmptyList
import spire.random.{Dist, Generator}

sealed trait OnomasticRule { self =>
  def apply(morphology: Morphology):Dist[List[Phones]] =
    Dist.gen(OnomasticRule.applyRule(morphology, self))
}
case object Mononym extends OnomasticRule
final case class Particle(vowel: Vowel) extends OnomasticRule
final case class Polynym(lhs: OnomasticRule, rhs: OnomasticRule) extends OnomasticRule

object OnomasticRule {

  private def elaborateRule(particle: Particle)(current: OnomasticRule)(gen: Generator): OnomasticRule =
    current match {
      case Mononym if gen.nextBoolean() =>
        elaborateRule(particle)(Polynym(Mononym, Mononym))(gen)
      case Polynym(Mononym, rhs) if gen.nextBoolean() =>
        elaborateRule(particle)(Polynym(Polynym(Mononym, particle), rhs))(gen)
      case Polynym(lhs, Mononym) if gen.nextBoolean() =>
        elaborateRule(particle)(Polynym(lhs, Polynym(particle, Mononym)))(gen)
      case Polynym(Polynym(lhs, _:Particle), Polynym(_:Particle, rhs)) =>
        elaborateRule(particle)(Polynym(lhs, Polynym(Mononym, rhs)))(gen)
      case x => x
    }

  def dist(morphology: Morphology): Dist[OnomasticRule] =
    for {
      particle <- morphology.phonology.anyVowel.map(Particle.apply)
      rule <- Dist.gen(elaborateRule(particle)(Mononym))
    } yield rule

  private def applyRule(morphology: Morphology, rule:OnomasticRule)(gen:Generator):List[Phones] =
    rule match {
      case Mononym ⇒ List(morphology.morpheme(gen))
      case Particle(v) ⇒ List(NonEmptyList.one(v))
      case Polynym(lhs, rhs) ⇒ applyRule(morphology, lhs)(gen) ++ applyRule(morphology, rhs)(gen)
    }
}
