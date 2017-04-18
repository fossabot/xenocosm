package xenocosm
package phonology
package data

import cats.Order
import cats.instances.int._
import spire.algebra.MetricSpace
import spire.random.Dist

/**
  * Consonant voicing
  */
sealed trait Voicing extends Any with Product with Serializable
case object Voiced extends Voicing
case object Voiceless extends Voicing

object Voicing {
  private lazy val all = List(Voiced, Voiceless)

  trait Instances {
    implicit val voicingHasOrder: Order[Voicing] = Order.by(x ⇒ all.indexOf(x))
    implicit val voicingHasDist: Dist[Voicing] = Dist.oneOf(all: _*)
    implicit val voicingHasMetricSpace: MetricSpace[Voicing, Int] =
      (v: Voicing, w: Voicing) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
