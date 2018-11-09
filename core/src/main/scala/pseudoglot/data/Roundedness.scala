package pseudoglot
package data

import cats.{Order, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.random.Dist

sealed abstract class Roundedness(val label:String) extends Product with Serializable
case object Rounded extends Roundedness("rounded")
case object Unrounded extends Roundedness("unrounded")

object Roundedness {
  lazy val all = List(Rounded, Unrounded)

  val parse:String => Either[String, Roundedness] =
    input => all.find(_.label === input) match {
      case Some(success) => Right(success)
      case None => Left(input)
    }

  trait Instances {
    implicit val roundednessHasShow: Show[Roundedness] = Show.show[Roundedness](_.label)
    implicit val roundednessHasOrder: Order[Roundedness] = Order.by(all.indexOf)
    implicit val roundednessHasDist: Dist[Roundedness] = Dist.oneOf(all: _*)
    implicit val roundednessHasMetricSpace: MetricSpace[Roundedness, Int] =
      (v: Roundedness, w: Roundedness) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
