package pseudoglot
package data

import cats.{Order, Show}
import cats.implicits._
import spire.algebra.MetricSpace
import spire.random.Dist

sealed abstract class Place(val label:String) extends Product with Serializable
case object Alveolar extends Place("alveolar")
case object Bilabial extends Place("bilabial")
case object Dental extends Place("dental")
case object Glottal extends Place("glottal")
case object LabioDental extends Place("labio-dental")
case object Palatal extends Place("palatal")
case object Pharyngeal extends Place("pharyngeal")
case object PostAlveolar extends Place("post-alveolar")
case object Retroflex extends Place("retroflex")
case object Uvular extends Place("uvular")
case object Velar extends Place("velar")

object Place {
  lazy val all = List(
    Bilabial,
    LabioDental,
    Dental,
    Alveolar,
    PostAlveolar,
    Retroflex,
    Palatal,
    Velar,
    Uvular,
    Pharyngeal,
    Glottal
  )

  val parse:String => Either[String, Place] =
    input => all.find(_.label === input) match {
      case Some(success) => Right(success)
      case None => Left(input)
    }

  trait Instances {
    implicit val placeHasShow: Show[Place] = Show.show[Place](_.label)
    implicit val placeHasOrder: Order[Place] = Order.by(all.indexOf)
    implicit val placeHasDist: Dist[Place] = Dist.oneOf(all: _*)
    implicit val placeHasMetricSpace: MetricSpace[Place, Int] =
      (v: Place, w: Place) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
