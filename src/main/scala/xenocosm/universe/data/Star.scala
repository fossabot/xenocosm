package xenocosm
package universe
package data

import java.security.MessageDigest
import cats.Eq
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy._
import squants.mass._
import squants.space._
import squants.thermal.{Kelvin, Temperature}

import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._

import MorganKeenan.instances._

final case class Star(galaxy:Galaxy, loc:Point3) {
  private def bytes:Array[Byte] = galaxy.digest ++ loc.digest(Parsecs)
  val digest:Array[Byte] = MessageDigest.getInstance("MD5").digest(bytes)
  private val gen:Generator = BurtleRot2.fromBytes(digest)

  val morganKeenan:MorganKeenan = implicitly[Dist[MorganKeenan]].apply(gen)
  val mass:Mass = Star.stellarMass(morganKeenan)(gen)
  val luminosity:Power = Star.stellarPower(morganKeenan)(gen)
  val radius:Length = Star.stellarRadius(morganKeenan)(gen)
  val temperature:Temperature = Star.stellarTemperature(morganKeenan)(gen)

}

object Star {

  def stellarMass(morganKeenan:MorganKeenan):Dist[Mass] = {
    val range = MorganKeenan.massRangeOf(morganKeenan)
    range.dist(range.lower, range.upper, SolarMasses(1))
  }

  def stellarPower(morganKeenan:MorganKeenan):Dist[Power] = {
    val range = MorganKeenan.powerRangeOf(morganKeenan)
    range.dist(range.lower, range.upper, SolarLuminosities(1))
  }

  def stellarRadius(morganKeenan:MorganKeenan):Dist[Length] = {
    val range = MorganKeenan.radiusRangeOf(morganKeenan)
    range.dist(range.lower, range.upper, SolarRadii(1))
  }

  def stellarTemperature(morganKeenan:MorganKeenan):Dist[Temperature] = {
    val range = MorganKeenan.temperatureRangeOf(morganKeenan)
    range.dist(range.lower, range.upper, Kelvin(100))
  }

  trait Instances {
    implicit val starHasEq:Eq[Star] = Eq.fromUniversalEquals[Star]
  }

  object instances extends Instances
}
