package xenocosm
package universe
package data

import cats.{Order, Show}
import cats.instances.int._
import spire.random.Dist
import squants.energy.Power
import squants.mass.Mass
import squants.space.Length
import squants.thermal.Temperature

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
  val all:Vector[MorganKeenan] = Vector(MK_A, MK_B, MK_C, MK_F, MK_G, MK_K, MK_L, MK_M, MK_O, MK_S, MK_T)

  private val sorted:Vector[MorganKeenan] =
    all.sortBy(mk ⇒ app.config.morganKeenan.frequencies.getOrElse(mk.get, 0d))

  def temperatureRangeOf(mk:MorganKeenan):Dist[Temperature] =
    app.config.morganKeenan.temperatures(mk.get).dist

  def powerRangeOf(mk:MorganKeenan):Dist[Power] =
    app.config.morganKeenan.luminosities(mk.get).dist

  def radiusRangeOf(mk:MorganKeenan):Dist[Length] =
    app.config.morganKeenan.radii(mk.get).dist

  def massRangeOf(mk:MorganKeenan):Dist[Mass] =
    app.config.morganKeenan.masses(mk.get).dist

  trait Instances {
    implicit val morganKeenanHasShow:Show[MorganKeenan] = Show.show(_.get)
    implicit val morganKeenanHasOrder:Order[MorganKeenan] = Order.by(x ⇒ sorted.indexOf(x))
    implicit val morganKeenanHasDist:Dist[MorganKeenan] = Dist.gen {
      gen ⇒ all(gen.nextInt(all.length))
    }
  }
  object instances extends Instances
}
