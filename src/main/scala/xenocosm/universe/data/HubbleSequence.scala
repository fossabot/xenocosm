package xenocosm
package universe
package data

import cats.{Order, Show}
import cats.instances.int._
import spire.random.Dist

/** The Hubble Sequence: A Galactic Classification System
  *
  * https://en.wikipedia.org/wiki/Hubble_sequence
  */
sealed abstract class HubbleSequence(val get:String) extends Product with Serializable
case object Elliptical extends HubbleSequence("E")
case object Lenticular extends HubbleSequence("S0")
case object RegularSpiral extends HubbleSequence("S")
case object BarredSpiral extends HubbleSequence("SB")

object HubbleSequence {
  lazy private val all:Vector[HubbleSequence] = Vector(Elliptical, Lenticular, RegularSpiral, BarredSpiral)

  trait Instances {
    implicit val hubbleSequenceHasShow:Show[HubbleSequence] = Show.show(_.get)
    implicit val hubbleSequenceHasOrder:Order[HubbleSequence] = Order.by(x ⇒ all.indexOf(x))
    implicit val hubbleSequenceHasDist:Dist[HubbleSequence] = Dist.gen(_.chooseFromSeq(all))
  }

  object instances extends Instances
}
