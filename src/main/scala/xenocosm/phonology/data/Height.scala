package xenocosm
package phonology
package data

import cats.Order
import cats.instances.int._
import spire.algebra.MetricSpace
import spire.random.Dist

/**
  * Vowel height
  */
sealed trait Height extends Any with Product with Serializable
case object Close extends Height
case object NearClose extends Height
case object CloseMid extends Height
case object Mid extends Height
case object OpenMid extends Height
case object NearOpen extends Height
case object Open extends Height

object Height {
  private lazy val all = List(Close, NearClose, CloseMid, Mid, OpenMid, NearOpen, Open)

  trait Instances {
    implicit val heightHasOrder: Order[Height] = Order.by(x ⇒ all.indexOf(x))
    implicit val heightHasDist: Dist[Height] = Dist.oneOf(all: _*)
    implicit val heightHasMetricSpace: MetricSpace[Height, Int] =
      (v: Height, w: Height) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
