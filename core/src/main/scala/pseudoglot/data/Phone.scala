package pseudoglot
package data

import cats.{Eq, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.syntax.metricSpace._

sealed trait Phone extends Any with Product with Serializable
case object NullPhoneme extends Phone
final case class Pulmonic(voicing: Voicing, place: Place, manner: Manner) extends Phone
final case class Vowel(roundedness: Roundedness, height: Height, backness: Backness) extends Phone

object Phone {
  import Backness.instances._
  import Height.instances._
  import Manner.instances._
  import Place.instances._
  import Roundedness.instances._
  import Voicing.instances._

  lazy val pulmonicCount:Int = Voicing.all.size * Place.all.size * Manner.all.size
  lazy val vowelCount:Int = Roundedness.all.size * Height.all.size * Backness.all.size

  trait NullPhonemeInstances {
    implicit val nullPhonemeHasEq:Eq[NullPhoneme.type] =
      Eq.fromUniversalEquals[NullPhoneme.type]

    implicit val nullPhonemeHasShow:Show[NullPhoneme.type] =
      Show.show(_ => "")

    // Chebyshev (chess) distance
    implicit val nullPhonemeHasMetricSpace:MetricSpace[NullPhoneme.type, Int] =
      (_: NullPhoneme.type, _: NullPhoneme.type) => 0
  }

  trait PulmonicInstances {
    implicit val pulmonicHasEq: Eq[Pulmonic] =
      Eq.fromUniversalEquals[Pulmonic]

    implicit val pulmonicHasShow: Show[Pulmonic] =
      Show.show(p => Seq(p.voicing.show, p.place.show, p.manner.show).mkString(":"))

    // Chebyshev (chess) distance
    implicit val pulmonicHasMetricSpace: MetricSpace[Pulmonic, Int] =
      (v: Pulmonic, w: Pulmonic) ⇒ Array[Int](
        v.voicing distance w.voicing,
        v.place distance w.place,
        v.manner distance w.manner
      ).max
  }

  trait VowelInstances {
    implicit val vowelHasEq: Eq[Vowel] =
      Eq.fromUniversalEquals[Vowel]

    implicit val vowelHasShow: Show[Vowel] =
      Show.show(v => Seq(v.roundedness.show, v.height.show, v.backness.show).mkString(":"))

    // Chebyshev (chess) distance
    implicit val vowelHasMetricSpace: MetricSpace[Vowel, Int] =
      (v: Vowel, w: Vowel) ⇒ Array[Int](
        v.roundedness distance w.roundedness,
        v.height distance w.height,
        v.backness distance w.backness
      ).max
  }

  trait Instances extends NullPhonemeInstances with PulmonicInstances with VowelInstances
  object instances extends Instances
}
