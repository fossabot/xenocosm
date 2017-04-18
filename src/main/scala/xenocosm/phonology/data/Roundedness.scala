package xenocosm
package phonology
package data

import cats.Order
import cats.instances.int._
import spire.algebra.MetricSpace
import spire.random.Dist

/**
  * Vowel roundedness
  */
sealed trait Roundedness extends Product with Serializable
case object Rounded extends Roundedness
case object Unrounded extends Roundedness

object Roundedness {
  private lazy val all = List(Rounded, Unrounded)

  trait Instances {
    implicit val roundednessHasOrder: Order[Roundedness] = Order.by(x ⇒ all.indexOf(x))
    implicit val roundednessHasDist: Dist[Roundedness] = Dist.oneOf(all: _*)
    implicit val roundednessHasMetricSpace: MetricSpace[Roundedness, Int] =
      (v: Roundedness, w: Roundedness) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
