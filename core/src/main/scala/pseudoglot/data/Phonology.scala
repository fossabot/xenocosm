package pseudoglot
package data

import cats.Eq
import spire.random.Dist

final case class Phonology(
  pulmonics: Vector[Pulmonic],
  vowels: Vector[Vowel],
  phonotactics: Set[PhonotacticRule])

object Phonology {
  import Phone.instances._
  import PhoneSeq.syntax._
  import PhonotacticRule.syntax._

  def applyRule(phonology:Phonology, rule:PhonotacticRule):Dist[Seq[Phone]] =
    Dist.gen { gen ⇒
      def anyPulmonic(xs:List[Phone]):List[Phone] =
        phonology.pulmonics.distPulmonic(gen).map(_ :: xs).getOrElse(xs)

      def anyVowel(xs:List[Phone]):List[Phone] =
        phonology.pulmonics.distPulmonic(gen).map(_ :: xs).getOrElse(xs)

      def loop(acc:List[Phone], rule:PhonotacticRule):List[Phone] =
        rule match {
          case Empty ⇒ acc
          case AnyPulmonic ⇒ anyPulmonic(acc)
          case AnyVowel ⇒ anyVowel(acc)
          case Literal(NullPhoneme) ⇒ acc
          case Literal(x:Pulmonic) ⇒ x :: acc
          case Literal(x:Vowel) ⇒ x :: acc
          case Choose(xs) ⇒ loop(acc, xs(gen.nextInt(xs.length)))
          case Concat(xs) ⇒ xs.flatMap(loop(acc, _))
        }

      loop(List.empty[Phone], rule)
    }

  def applyRules(phonology:Phonology, rules:Seq[PhonotacticRule]):Dist[Seq[Phone]] =
    Dist.gen(gen => rules.flatMap(rule => applyRule(phonology, rule)(gen)))

  def anyRule(phonology:Phonology):Dist[PhonotacticRule] =
    Dist.oneOf(phonology.phonotactics.toVector:_*)

  def ruleWithVowel(phonology:Phonology):Dist[PhonotacticRule] =
    Dist.oneOf(phonology.phonotactics.toVector.filter(_.hasVowel):_*)

  trait Instances {
    implicit val phonologyHasEq:Eq[Phonology] = Eq.fromUniversalEquals[Phonology]
    implicit val phonologyHasDist:Dist[Phonology] =
      for {
        pulmonicCount ← Dist.intrange(1, Phone.pulmonicCount)
        vowelCount ← Dist.intrange(1, Phone.vowelCount)
        ruleCount ← Dist.intrange(1, 100)
        pulmonics ← Dist.apply[Pulmonic].pack(pulmonicCount).map(_.toVector.distinct)
        vowels ← Dist.apply[Vowel].pack(vowelCount).map(_.toVector.distinct)
        phonotactics ← (pulmonics ++ vowels).distRule.pack(ruleCount)
      } yield Phonology(pulmonics, vowels, phonotactics.toSet)
  }
  object instances extends Instances
}
