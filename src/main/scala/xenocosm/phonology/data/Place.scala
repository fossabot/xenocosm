package xenocosm
package phonology
package data

import cats.Order
import cats.instances.int._
import spire.algebra.MetricSpace
import spire.random.Dist

/**
  * Consonant place of articulation
  */
sealed trait Place extends Product with Serializable
case object Alveolar extends Place
case object Bilabial extends Place
case object Dental extends Place
case object Glottal extends Place
case object LabioDental extends Place
case object Palatal extends Place
case object Pharyngeal extends Place
case object PostAlveolar extends Place
case object Retroflex extends Place
case object Uvular extends Place
case object Velar extends Place

object Place {
  private lazy val all = List(
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

  trait Instances {
    implicit val placeHasOrder: Order[Place] = Order.by(x ⇒ all.indexOf(x))
    implicit val placeHasDist: Dist[Place] = Dist.oneOf(all: _*)
    implicit val placeHasMetricSpace: MetricSpace[Place, Int] =
      (v: Place, w: Place) ⇒ math.abs(all.indexOf(v) - all.indexOf(w))
  }
  object instances extends Instances
}
