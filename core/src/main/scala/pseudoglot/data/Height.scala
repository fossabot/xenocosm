package pseudoglot
package data

import cats.{Order, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.random.Dist

sealed abstract class Height(val label:String) extends Product with Serializable
case object Close extends Height("close")
case object NearClose extends Height("near-close")
case object CloseMid extends Height("close-mid")
case object Mid extends Height("mid")
case object OpenMid extends Height("open-mid")
case object NearOpen extends Height("near-open")
case object Open extends Height("open")

object Height {
  lazy val all = List(Close, NearClose, CloseMid, Mid, OpenMid, NearOpen, Open)

  val parse:String => Either[String, Height] =
    input => all.find(_.label === input) match {
      case Some(success) => Right(success)
      case None => Left(input)
    }

  trait Instances {
    implicit val heightHasShow: Show[Height] = Show.show[Height](_.label)
    implicit val heightHasOrder: Order[Height] = Order.by(all.indexOf)
    implicit val heightHasDist: Dist[Height] = Dist.oneOf(all: _*)
    implicit val heightHasMetricSpace: MetricSpace[Height, Int] =
      (v: Height, w: Height) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
