package xenocosm
package phonology
package data

import cats.Eq
import spire.algebra.MetricSpace
import spire.random.Dist
import spire.syntax.metricSpace._
import xenocosm.phonology.data.Backness.instances._
import xenocosm.phonology.data.Height.instances._
import xenocosm.phonology.data.Manner.instances._
import xenocosm.phonology.data.Place.instances._
import xenocosm.phonology.data.Roundedness.instances._
import xenocosm.phonology.data.Voicing.instances._

sealed trait Phone extends Any with Product with Serializable

final case class Pulmonic(voicing: Voicing, place: Place, manner: Manner) extends Phone

object Pulmonic {
  trait Instances {
    implicit val pulmonicHasEq: Eq[Pulmonic] = Eq.fromUniversalEquals[Pulmonic]
    implicit val pulmonicHasDist: Dist[Pulmonic] =
      implicitly[Dist[Voicing]].
        zip(implicitly[Dist[Place]]).
        zipWith(implicitly[Dist[Manner]])({
          case ((voicing, place), manner) ⇒ Pulmonic(voicing, place, manner)
        })
    // Chebyshev (chess) distance
    implicit val pulmonicHasMetricSpace: MetricSpace[Pulmonic, Int] =
      (v: Pulmonic, w: Pulmonic) ⇒ Array(
        v.voicing distance w.voicing,
        v.place distance w.place,
        v.manner distance w.manner
      ).max
  }
  object instances extends Instances
}

final case class Vowel(roundedness: Roundedness, height: Height, backness: Backness) extends Phone

object Vowel {
  trait Instances {
    implicit val vowelHasEq: Eq[Vowel] = Eq.fromUniversalEquals[Vowel]
    implicit val vowelHasDist: Dist[Vowel] =
      implicitly[Dist[Roundedness]].
        zip(implicitly[Dist[Height]]).
        zipWith(implicitly[Dist[Backness]])({
          case ((roundedness, height), backness) ⇒ Vowel(roundedness, height, backness)
        })
    // Chebyshev (chess) distance
    implicit val vowelHasMetricSpace: MetricSpace[Vowel, Int] =
      (v: Vowel, w: Vowel) ⇒ Array(
        v.roundedness distance w.roundedness,
        v.height distance w.height,
        v.backness distance w.backness
      ).max
  }
  object instances extends Instances
}
