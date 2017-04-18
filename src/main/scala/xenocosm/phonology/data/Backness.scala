package xenocosm
package phonology
package data

import cats.Order
import cats.instances.int._
import spire.algebra.MetricSpace
import spire.random.Dist

/**
  * Vowel backness
  */
sealed trait Backness extends Any with Product with Serializable
case object Front extends Backness
case object NearFront extends Backness
case object Central extends Backness
case object NearBack extends Backness
case object Back extends Backness

object Backness {
  private lazy val all = List(Front, NearFront, Central, NearBack, Back)

  trait Instances {
    implicit val backnessHasOrder: Order[Backness] = Order.by(x ⇒ all.indexOf(x))
    implicit val backnessHasDist: Dist[Backness] = Dist.oneOf(all: _*)
    implicit val backnessHasMetricSpace: MetricSpace[Backness, Int] =
      (v: Backness, w: Backness) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
