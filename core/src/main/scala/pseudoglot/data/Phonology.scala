package pseudoglot
package data

import cats.Eq
import cats.data.NonEmptyList
import spire.random.Dist

final case class Phonology(pulmonics:NonEmptyList[Pulmonic], vowels:NonEmptyList[Vowel], phonotactics:PhonotacticRules)

object Phonology {
  import Phone.instances._

  private val anyPulmonic:Phonology => Dist[Pulmonic] = phonology => Dist.oneOf(phonology.pulmonics.toList: _*)
  private val anyVowel:Phonology => Dist[Vowel] = phonology => Dist.oneOf(phonology.vowels.toList: _*)

  def applyRule(phonology:Phonology, rule:PhonotacticRule):Dist[Phones] =
    Dist.gen { gen ⇒
      def loop(acc:List[Phone], rule:PhonotacticRule):List[Phone] =
        rule match {
          case Empty ⇒ acc
          case AnyPulmonic ⇒ anyPulmonic(phonology)(gen) :: acc
          case AnyVowel ⇒ anyVowel(phonology)(gen) :: acc
          case Literal(NullPhoneme) ⇒ acc
          case Literal(x:Pulmonic) ⇒ x :: acc
          case Literal(x:Vowel) ⇒ x :: acc
          case Choose(lhs, rhs) ⇒ loop(acc, gen.chooseFromArray(Array(lhs, rhs)))
          case Concat(lhs, rhs) ⇒ loop(Nil, lhs) ++ loop(Nil, rhs) ++ acc
        }

      loop(List.empty[Phone], rule) match {
        case Nil => NonEmptyList(anyVowel(phonology)(gen), List.empty[Phone])
        case x :: xs => NonEmptyList(x, xs)
      }
    }

  def dist(implicit magic:Magic):Dist[Phonology] =
    for {
      pulmonicCount <- Dist.intrange _ tupled magic.pulmonics
      vowelCount    <- Dist.intrange _ tupled magic.vowels
      ruleCount     <- Dist.intrange _ tupled magic.rules
      pulmonics     <- Dist.apply[Pulmonic].pack(pulmonicCount).map(_.distinct.toList)
      vowels        <- Dist.apply[Vowel].pack(vowelCount).map(_.distinct.toList)
      phonotactics  <- PhonotacticRule.ruleFromPhones(pulmonics ++ vowels).pack(ruleCount).map(_.distinct.toList)
    } yield Phonology(
      NonEmptyList.fromListUnsafe(pulmonics),
      NonEmptyList.fromListUnsafe(vowels),
      NonEmptyList.fromListUnsafe(phonotactics)
    )

  trait Instances {
    implicit val phonologyHasEq:Eq[Phonology] = Eq.fromUniversalEquals[Phonology]
  }
  object instances extends Instances
}
