package xenocosm
package universe
package data

import cats.{Order, Show}
import cats.instances.int._
import spire.math.Bounded
import spire.random.Dist
import squants.energy._
import squants.mass._
import squants.space._
import squants.thermal.{Kelvin, Temperature}

import xenocosm.instances.interop._

/** Morgan-Keenan Stellar Classification
  *
  * http://www.handprint.com/ASTRO/specclass.html
  */
sealed abstract class MorganKeenan(val get:String) extends Product with Serializable
sealed trait MainSequence
sealed trait NonMainSequence
case object MK_O extends MorganKeenan("O") with MainSequence
case object MK_B extends MorganKeenan("B") with MainSequence
case object MK_A extends MorganKeenan("A") with MainSequence
case object MK_F extends MorganKeenan("F") with MainSequence
case object MK_G extends MorganKeenan("G") with MainSequence
case object MK_K extends MorganKeenan("K") with MainSequence
case object MK_M extends MorganKeenan("M") with MainSequence
case object MK_C extends MorganKeenan("C") with NonMainSequence
case object MK_S extends MorganKeenan("S") with MainSequence
case object MK_L extends MorganKeenan("L") with NonMainSequence
case object MK_T extends MorganKeenan("T") with NonMainSequence

object MorganKeenan {

  /** Classifications with main sequence frequency
    */
  private val freq:Map[MorganKeenan, Double] = Map(
    MK_K → 0.276d,
    MK_F → 0.22d,
    MK_G → 0.196d,
    MK_A → 0.16d,
    MK_B → 0.089d,
    MK_M → 0.05d,
    MK_O → 0.0023d,
    MK_S → 0.0014d,
    MK_C → 0.000001d,
    MK_L → 0.0000001d,
    MK_T → 0.00000001d
  )

  private val all:Vector[MorganKeenan] = freq.keys.toVector

  /** Temperature ranges for the Morgan-Keenan classification.
    *
    * http://www.handprint.com/ASTRO/specclass.html
    */
  def temperatureRangeOf(mk:MorganKeenan):Bounded[Temperature] =
    mk match {
      case MK_O ⇒ Bounded(Kelvin(30000), Kelvin(Int.MaxValue), 0)
      case MK_B ⇒ Bounded(Kelvin(10000), Kelvin(30000), 0)
      case MK_A ⇒ Bounded(Kelvin(7300), Kelvin(10000), 0)
      case MK_F ⇒ Bounded(Kelvin(6000), Kelvin(7300), 0)
      case MK_G ⇒ Bounded(Kelvin(5300), Kelvin(6000), 0)
      case MK_K ⇒ Bounded(Kelvin(3800), Kelvin(5300), 0)
      case MK_M ⇒ Bounded(Kelvin(2500), Kelvin(3800), 0)
      case MK_C ⇒ Bounded(Kelvin(2400), Kelvin(3200), 0)
      case MK_S ⇒ Bounded(Kelvin(2400), Kelvin(3500), 0)
      case MK_L ⇒ Bounded(Kelvin(1300), Kelvin(2100), 0)
      case MK_T ⇒ Bounded(Kelvin(600), Kelvin(1300), 0)
    }

  /** Solar luminosity ranges for the Morgan-Keenan classification.
    *
    * http://www.handprint.com/ASTRO/specclass.html
    */
  def powerRangeOf(mk:MorganKeenan):Bounded[Power] =
    mk match {
      case MK_O ⇒ Bounded(SolarLuminosities(53000), SolarLuminosities(1000000), 0)
      case MK_B ⇒ Bounded(SolarLuminosities(54), SolarLuminosities(52500), 0)
      case MK_A ⇒ Bounded(SolarLuminosities(6.5), SolarLuminosities(54), 0)
      case MK_F ⇒ Bounded(SolarLuminosities(1.5), SolarLuminosities(6.5), 0)
      case MK_G ⇒ Bounded(SolarLuminosities(0.4), SolarLuminosities(1.5), 0)
      case MK_K ⇒ Bounded(SolarLuminosities(0.08), SolarLuminosities(0.4), 0)
      case MK_M ⇒ Bounded(SolarLuminosities(0.00031622776), SolarLuminosities(0.08), 0)
      case MK_C ⇒ Bounded(SolarLuminosities(0), SolarLuminosities(0.001), 0)
      case MK_S ⇒ Bounded(SolarLuminosities(0), SolarLuminosities(0.001), 0)
      case MK_L ⇒ Bounded(SolarLuminosities(0.00003981071), SolarLuminosities(0.00019952623), 0)
      case MK_T ⇒ Bounded(SolarLuminosities(0.00000630957), SolarLuminosities(0.00003162277), 0)
    }

  /** Solar radius ranges for the Morgan-Keenan classification frequencies.
    *
    * @see http://www.handprint.com/ASTRO/specclass.html
    */
  def radiusRangeOf(mk:MorganKeenan):Bounded[Length] =
    mk match {
      case MK_O ⇒ Bounded(SolarRadii(6.6), SolarRadii(18), 0)
      case MK_B ⇒ Bounded(SolarRadii(1.8), SolarRadii(6.6), 0)
      case MK_A ⇒ Bounded(SolarRadii(1.4), SolarRadii(1.8), 0)
      case MK_F ⇒ Bounded(SolarRadii(1.15), SolarRadii(1.4), 0)
      case MK_G ⇒ Bounded(SolarRadii(0.96), SolarRadii(1.15), 0)
      case MK_K ⇒ Bounded(SolarRadii(0.7), SolarRadii(0.96), 0)
      case MK_M ⇒ Bounded(SolarRadii(0), SolarRadii(0.7), 0)
      case MK_C ⇒ Bounded(SolarRadii(220), SolarRadii(550), 0)
      case MK_S ⇒ Bounded(SolarRadii(0), SolarRadii(0.7), 0)
      case MK_L ⇒ Bounded(SolarRadii(0), SolarRadii(0.2), 0)
      case MK_T ⇒ Bounded(SolarRadii(0), SolarRadii(0.2), 0)
    }

  /** Solar mass ranges for the Morgan-Keenan classification.
    *
    * http://www.handprint.com/ASTRO/specclass.html
    */
  def massRangeOf(mk:MorganKeenan):Bounded[Mass] =
    mk match {
      case MK_O ⇒ Bounded(SolarMasses(18), SolarMasses(150), 0)
      case MK_B ⇒ Bounded(SolarMasses(2.9), SolarMasses(18), 0)
      case MK_A ⇒ Bounded(SolarMasses(1.6), SolarMasses(2.9), 0)
      case MK_F ⇒ Bounded(SolarMasses(1.05), SolarMasses(1.6), 0)
      case MK_G ⇒ Bounded(SolarMasses(0.8), SolarMasses(1.05), 0)
      case MK_K ⇒ Bounded(SolarMasses(0.5), SolarMasses(0.8), 0)
      case MK_M ⇒ Bounded(SolarMasses(0.07), SolarMasses(0.5), 0)
      case MK_C ⇒ Bounded(SolarMasses(0), SolarMasses(1.1), 0)
      case MK_S ⇒ Bounded(SolarMasses(0), SolarMasses(0.8), 0)
      case MK_L ⇒ Bounded(SolarMasses(0.075), SolarMasses(0.45), 0)
      case MK_T ⇒ Bounded(SolarMasses(0.012), SolarMasses(0.075), 0)
    }

  trait Instances {
    implicit val morganKeenanHasShow:Show[MorganKeenan] = Show.show(_.get)
    implicit val morganKeenanHasOrder:Order[MorganKeenan] = Order.by(x ⇒ all.indexOf(x))
    implicit val morganKeenanHasDist:Dist[MorganKeenan] =
      Dist.double.map(x ⇒ freq.find({ case (_, y) ⇒ x > y }).map(_._1).getOrElse(MK_O))
  }
  object instances extends Instances
}
