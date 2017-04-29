package xenocosm
package universe
package data

import cats.Eq
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy._
import squants.mass._
import squants.space._
import squants.thermal.{Kelvin, Temperature}

import xenocosm.geometry.data.Point3
import xenocosm.interop.instances._
import xenocosm.phonology.data.Phonology
import xenocosm.phonology.instances._

import MorganKeenan.instances._

final case class Star(galaxy:Galaxy, loc:Point3) { self ⇒
  private val gen:Generator = Star.gen(self)
  val morganKeenan:MorganKeenan = implicitly[Dist[MorganKeenan]].apply(gen)
  val mass:Mass = Star.stellarMass(morganKeenan)(gen)
  val luminosity:Power = Star.stellarPower(morganKeenan)(gen)
  val radius:Length = Star.stellarRadius(morganKeenan)(gen)
  val temperature:Temperature = Star.stellarTemperature(morganKeenan)(gen)
  val phonology:Phonology = gen.next[Phonology]
}

object Star {
  val bytes:Star ⇒ Array[Byte] = star ⇒
    Galaxy.bytes(star.galaxy) ++ Point3.bytes(AstronomicalUnits)(star.loc)

  val gen:Star ⇒ Generator = BurtleRot2.fromBytes _ compose Digest.md5 compose bytes

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
