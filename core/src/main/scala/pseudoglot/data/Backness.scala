package pseudoglot
package data

import cats.{Order, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.random.Dist

sealed abstract class Backness(val label:String) extends Product with Serializable
case object Front extends Backness("front")
case object NearFront extends Backness("near-front")
case object Central extends Backness("central")
case object NearBack extends Backness("near-back")
case object Back extends Backness("back")

object Backness {
  lazy val all = List(Front, NearFront, Central, NearBack, Back)
  def parse(in:String):Either[String, Backness] =
    all.find(_.label === in) match {
      case Some(success) => Right(success)
      case None => Left(in)
    }

  trait Instances {
    implicit val backnessHasShow: Show[Backness] = Show.show[Backness](_.label)
    implicit val backnessHasOrder: Order[Backness] = Order.by(x ⇒ all.indexOf(x))
    implicit val backnessHasDist: Dist[Backness] = Dist.oneOf(all: _*)
    implicit val backnessHasMetricSpace: MetricSpace[Backness, Int] =
      (v: Backness, w: Backness) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
