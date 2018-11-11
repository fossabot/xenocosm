package pseudoglot
package data

import cats.Eq
import cats.data.NonEmptyList
import spire.random.Dist

final case class Phonology(pulmonics:NonEmptyList[Pulmonic], vowels:NonEmptyList[Vowel], phonotactics:PhonotacticRules) {
  lazy private val indexedPulmonics = Phonology.normalIndex(pulmonics)
  lazy private val indexedVowels = Phonology.normalIndex(vowels)
  lazy private val indexedRules = Phonology.normalIndex(phonotactics)

  lazy val anyPulmonic:Dist[Pulmonic] = Phonology.normalDist(indexedPulmonics)
  lazy val anyVowel:Dist[Vowel] = Phonology.normalDist(indexedVowels)
  lazy val anyRule:Dist[PhonotacticRule] = Phonology.normalDist(indexedRules)
  lazy val syllable:Dist[Phones] = anyRule.flatMap(Phonology.applyRule(this, _))
}

object Phonology {

  def normalIndex[A](xs: NonEmptyList[A]):NonEmptyList[(Double, A)] =
    xs.zipWithIndex.map({
      case (x, i) => (((i.toDouble / xs.size) * 2) - 1, x)
    })

  def normalDist[A](ixs: NonEmptyList[(Double, A)]): Dist[A] = Dist.gen {
    gen =>
      val g = gen.nextGaussian()
      ixs.filter(_._1 < g) match {
        case Nil => ixs.toList.minBy(_._1)._2
        case iys => iys.maxBy(_._1)._2
      }
  }

  def applyRule(phonology:Phonology, rule:PhonotacticRule):Dist[Phones] =
    Dist.gen { gen ⇒
      def loop(acc:List[Phone], rule:PhonotacticRule):List[Phone] =
        rule match {
          case Empty ⇒ acc
          case AnyPulmonic ⇒ phonology.anyPulmonic(gen) :: acc
          case AnyVowel ⇒ phonology.anyVowel(gen) :: acc
          case Literal(NullPhoneme) ⇒ acc
          case Literal(x:Pulmonic) ⇒ x :: acc
          case Literal(x:Vowel) ⇒ x :: acc
          case Choose(lhs, rhs) ⇒ loop(acc, gen.chooseFromArray(Array(lhs, rhs)))
          case Concat(lhs, rhs) ⇒ loop(Nil, lhs) ++ loop(Nil, rhs) ++ acc
        }

      loop(List.empty[Phone], rule) match {
        case Nil => NonEmptyList(phonology.anyVowel(gen), List.empty[Phone])
        case x :: xs => NonEmptyList(x, xs)
      }
    }

  private def percentile50[A](xs: Iterable[A]): Dist[List[A]] =
    Dist.gen { gen =>
      xs
        .zip(gen.generateGaussians(xs.size))
        .filter({ case (_, i) => i > -0.5 && i < 0.5 })
        .toList
        .sortBy(_._2)
        .map(_._1)
    }

  val dist:Dist[Phonology] =
    for {
      pulmonics    <- percentile50(IPA.pulmonics.keys)
      vowels       <- percentile50(IPA.vowels.keys)
      phonotactics <- PhonotacticRule.rulesFromPhones(pulmonics, vowels)
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
