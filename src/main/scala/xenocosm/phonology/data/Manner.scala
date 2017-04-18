package xenocosm
package phonology
package data

import cats.Order
import cats.instances.int._
import spire.algebra.MetricSpace
import spire.random.Dist

/**
  * Consonant manner of articulation
  */
sealed trait Manner extends Any with Product with Serializable
case object Approximant extends Manner
case object Fricative extends Manner
case object LateralFricative extends Manner
case object LateralApproximant extends Manner
case object Nasal extends Manner
case object Plosive extends Manner
case object TapOrFlap extends Manner
case object Trill extends Manner

object Manner {
  private lazy val all = List(
    Plosive,
    Nasal,
    Trill,
    TapOrFlap,
    Fricative,
    LateralFricative,
    Approximant,
    LateralApproximant
  )

  trait Instances {
    implicit val mannerHasOrder: Order[Manner] = Order.by(x ⇒ all.indexOf(x))
    implicit val mannerHasDist: Dist[Manner] = Dist.oneOf(all: _*)
    implicit val mannerHasMetricSpace: MetricSpace[Manner, Int] =
      (v: Manner, w: Manner) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
