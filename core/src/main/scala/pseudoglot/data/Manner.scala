package pseudoglot
package data

import cats.{Order, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.random.Dist

sealed abstract class Manner(val label:String) extends Product with Serializable
case object Approximant extends Manner("approximant")
case object Fricative extends Manner("fricative")
case object LateralFricative extends Manner("lateral-fricative")
case object LateralApproximant extends Manner("lateral-approximant")
case object Nasal extends Manner("nasal")
case object Plosive extends Manner("plosive")
case object TapOrFlap extends Manner("tap-or-flap")
case object Trill extends Manner("trill")

object Manner {
  lazy val all = List(
    Plosive,
    Nasal,
    Trill,
    TapOrFlap,
    Fricative,
    LateralFricative,
    Approximant,
    LateralApproximant
  )
  def parse(in:String):Either[String, Manner] =
    all.find(_.label === in) match {
      case Some(success) => Right(success)
      case None => Left(in)
    }

  trait Instances {
    implicit val mannerHasShow: Show[Manner] = Show.show[Manner](_.label)
    implicit val mannerHasOrder: Order[Manner] = Order.by(x ⇒ all.indexOf(x))
    implicit val mannerHasDist: Dist[Manner] = Dist.oneOf(all: _*)
    implicit val mannerHasMetricSpace: MetricSpace[Manner, Int] =
      (v: Manner, w: Manner) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
