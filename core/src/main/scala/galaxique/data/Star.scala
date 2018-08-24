package galaxique
package data

import cats.Eq
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy.{Power, SolarLuminosities}
import squants.mass.{Density, Mass, SolarMasses}
import squants.space._
import squants.thermal.{Kelvin, Temperature}
import MorganKeenan.instances._

final case class Star(galaxy:Galaxy, loc:Point3) { self =>
  private val gen:Generator = Star.gen(self)
  val mk:MorganKeenan = Dist[MorganKeenan].apply(gen)
  val mass:Mass = mk.distMass(gen).in(SolarMasses)
  val luminosity:Power = mk.distLuminosity(gen).in(SolarLuminosities)
  val radius:Length = mk.distRadius(gen).in(SolarRadii)
  val temperature:Temperature = mk.distTemperature(gen).in(Kelvin)
  lazy val μ:Double = mass.toKilograms * G
  lazy val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  lazy val density:Density = mass / volume
  def rocheLimit(a:Density):Length = Math.pow(density / a, 1d / 3d) * 1.26 * radius
  def rocheLimit(a:Planet):Length = rocheLimit(a.density)
}

object Star {
  lazy val scale:Length = AstronomicalUnits(1)

  private[data] val bytes:Star => Array[Byte] = star =>
    Galaxy.bytes(star.galaxy) ++ Point3.bytes(Parsecs)(star.loc)
  private val gen:Star => Generator = BurtleRot2.fromBytes _ compose bytes

  // Scale a double from [0.0, 1.0) to correspond to a point within the stellar region
  //FIXME: Calculate the heliopause
  private val toCoordinate:Star => Double => Length = star => d =>
    scale * ((AstronomicalUnits(121) * ((2 * d) - 1)) / scale).floor

  //FIXME: Calculate z-axis
  val point:Star => Dist[Point3] = star =>
    for {
      x <- Dist.double
      y <- Dist.double
    } yield Point3(
      toCoordinate(star)(x),
      toCoordinate(star)(y),
      AstronomicalUnits(0)
    )

  trait Instances {
    implicit val starHasEq:Eq[Star] = Eq.fromUniversalEquals[Star]

    //FIXME: Incorporate the roche limit into `locate()`
    implicit val starHasSparseSpace:SparseSpace3[Star, Planet] =
      SparseSpace3.fromStandardProof[Star, Planet](AstronomicalUnits, Star.scale)(Planet.apply)(bytes)
  }
  object instances extends Instances
}
