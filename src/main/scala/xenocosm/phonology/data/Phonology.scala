package xenocosm
package phonology
package data

import cats.Eq
import spire.random.Dist

import Pulmonic.instances._
import Vowel.instances._

/**
  * Represents a phonology for a language
  */
final case class Phonology(pulmonics: Vector[Pulmonic], vowels: Vector[Vowel], phonotactics: Set[PhonotacticRule])

object Phonology {

  def word(phonology:Phonology):Dist[Vector[Phone]] =
    Dist.gen { gen ⇒

      def loop(acc:Vector[Phone]):Vector[Phone] =
        if (gen.nextBoolean()) {
          loop(PhonotacticRule.syllable(phonology)(gen) ++ acc)
        } else {
          acc
        }

      loop(PhonotacticRule.syllable(phonology)(gen))
    }

  trait Instances {
    implicit val phonologyHasEq:Eq[Phonology] = Eq.fromUniversalEquals[Phonology]
    implicit val phonologyHasDist:Dist[Phonology] =
      for {
        pulmonicCount ← Dist.ubyte
        pulmonics ← implicitly[Dist[Pulmonic]].pack(pulmonicCount.toInt + 1)
        vowelCount ← Dist.ubyte
        vowels ← implicitly[Dist[Vowel]].pack(vowelCount.toInt + 1)
        phonotacticRuleCount ← Dist.ubyte
        phonotactics ← PhonotacticRule.dist(pulmonics, vowels).pack(phonotacticRuleCount.toInt + 1)
      } yield Phonology(
        pulmonics.distinct.toVector,
        vowels.distinct.toVector,
        phonotactics.distinct.toSet
      )
  }
  object instances extends Instances
}
