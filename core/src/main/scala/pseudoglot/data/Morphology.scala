package pseudoglot
package data

import spire.random.Dist

final case class Morphology(phonology: Phonology) { self =>
  val morpheme:Dist[Phones] = Morphology.morpheme(self)
}

object Morphology {
  import spire.syntax.metricSpace._
  import Backness.instances._
  import Height.instances._
  import Place.instances._

  val dist: Dist[Morphology] = Phonology.dist.map(Morphology.apply)

  def fuse(lhs: Phones, rhs: Phones): Phones =
    (lhs.last, rhs.head) match {
      case (Pulmonic(_, a, _), Pulmonic(_, b, _)) if (a distance b) <= 1 =>
        lhs ++ rhs.tail
      case (Vowel(_, a, _), Vowel(_, b, _)) if (a distance b) <= 1 =>
        lhs ++ rhs.tail
      case (Vowel(_, _, a), Vowel(_, _, b)) if (a distance b) <= 1 =>
        lhs ++ rhs.tail
      case _ => lhs ++ rhs.toList
    }

  def morpheme(morphology: Morphology): Dist[Phones] =
    for {
      n <- Dist.intrange(1, 3)
      phones <- morphology.phonology.syllable.pack(n)
    } yield phones.reduce(fuse)
}
