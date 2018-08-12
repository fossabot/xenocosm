package pseudoglot
package data

import cats.{Order, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.random.Dist

sealed abstract class Voicing(val label:String) extends Product with Serializable
case object Voiced extends Voicing("voiced")
case object Murmered extends Voicing("murmered")
case object Voiceless extends Voicing("unvoiced")

object Voicing {
  lazy val all = List(Voiced, Murmered, Voiceless)

  val parse:String => Either[String, Voicing] =
    input => all.find(_.label === input) match {
      case Some(success) => Right(success)
      case None => Left(input)
    }

  trait Instances {
    implicit val voicingHasShow: Show[Voicing] = Show.show[Voicing](_.label)
    implicit val voicingHasOrder: Order[Voicing] = Order.by(all.indexOf)
    implicit val voicingHasDist: Dist[Voicing] = Dist.oneOf(all: _*)
    implicit val voicingHasMetricSpace: MetricSpace[Voicing, Int] =
      (v: Voicing, w: Voicing) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
