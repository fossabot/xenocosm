package pseudoglot
package data

import cats.Eq
import spire.random.Dist

final case class Morphology(phonology: Phonology) { self =>
  val morpheme:Dist[Phones] = Morphology.morpheme(self)
}

object Morphology {
  import spire.syntax.metricSpace._
  import Phone.instances._

  val dist: Dist[Morphology] = Phonology.dist.map(Morphology.apply)

  def fuse(lhs: Phones, rhs: Phones): Phones =
    (lhs.last, rhs.head) match {
      case (a:Pulmonic, b:Pulmonic) if (a distance b) <= 2 =>
        lhs ++ rhs.tail
      case (a:Vowel, b:Vowel) if (a distance b) <= 2 =>
        lhs ++ rhs.tail
      case _ => lhs ++ rhs.toList
    }

  def morpheme(morphology: Morphology): Dist[Phones] =
    for {
      n <- Dist.gaussian[Double](1.0, 0.5).map(_.round.toInt + 1)
      phones <- morphology.phonology.syllable.pack(n)
    } yield phones.reduce(fuse)

  trait Instances {
    implicit val morphologyRuleHasEq:Eq[Morphology] =
      Eq.fromUniversalEquals[Morphology]
  }
  object instances extends Instances
}
