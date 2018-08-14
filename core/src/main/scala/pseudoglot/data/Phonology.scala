package pseudoglot
package data

import cats.Eq
import spire.random.Dist

final case class Phonology(pulmonics:List[Pulmonic], vowels:List[Vowel], phonotactics:PhonotacticRules)

object Phonology {
  import Phone.instances._

  private val anyPulmonic:Phonology => Dist[Pulmonic] = phonology => Dist.oneOf(phonology.pulmonics: _*)
  private val anyVowel:Phonology => Dist[Vowel] = phonology => Dist.oneOf(phonology.vowels: _*)

  def applyRule(phonology:Phonology, rule:PhonotacticRule):Dist[Phones] =
    Dist.gen { gen ⇒
      def loop(acc:Phones, rule:PhonotacticRule):Phones =
        rule match {
          case Empty ⇒ acc
          case AnyPulmonic ⇒ anyPulmonic(phonology)(gen) :: acc
          case AnyVowel ⇒ anyVowel(phonology)(gen) :: acc
          case Literal(NullPhoneme) ⇒ acc
          case Literal(x:Pulmonic) ⇒ x :: acc
          case Literal(x:Vowel) ⇒ x :: acc
          case Choose(xs) ⇒ loop(acc, xs(gen.nextInt(xs.length)))
          case Concat(xs) ⇒ xs.flatMap(loop(acc, _))
        }

      loop(List.empty[Phone], rule)
    }

  def dist(implicit magic:Magic):Dist[Phonology] =
    for {
      pulmonicCount <- Dist.intrange _ tupled magic.pulmonics
      vowelCount    <- Dist.intrange _ tupled magic.vowels
      ruleCount     <- Dist.intrange _ tupled magic.rules
      pulmonics     <- Dist.apply[Pulmonic].pack(pulmonicCount).map(_.distinct.toList)
      vowels        <- Dist.apply[Vowel].pack(vowelCount).map(_.distinct.toList)
      phonotactics  <- PhonotacticRule.ruleFromPhones(pulmonics ++ vowels).pack(ruleCount)
    } yield Phonology(pulmonics, vowels, phonotactics.distinct.toSet)

  trait Instances {
    implicit val phonologyHasEq:Eq[Phonology] = Eq.fromUniversalEquals[Phonology]
  }
  object instances extends Instances
}
