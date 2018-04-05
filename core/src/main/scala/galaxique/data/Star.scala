package galaxique
package data

import cats.Eq
import spire.math.Interval
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy.Power
import squants.mass.{Density, Mass}
import squants.space.{AstronomicalUnits, Length, Parsecs, Volume}
import squants.thermal.Temperature
import MorganKeenan.instances._

final case class Star(galaxy:Galaxy, loc:Point3) { self =>
  private val gen:Generator = Star.gen(self)
  val mk:MorganKeenan = Dist[MorganKeenan].apply(gen)
  val mass:Mass = mk.distMass(gen)
  val luminosity:Power = mk.distLuminosity(gen)
  val radius:Length = mk.distRadius(gen)
  val temperature:Temperature = mk.distTemperature(gen)
  lazy val μ:Double = mass.toKilograms * G
  lazy val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  lazy val density:Density = mass / volume
  def rocheLimit(a:Density):Length = Math.pow(density / a, 1d / 3d) * 1.26 * radius
  def rocheLimit(a:Planet):Length = rocheLimit(a.density)
}

object Star {
  import interop.length._
  import Galaxy.instances._

  private[data] val bytes:Star => Array[Byte] = star =>
    Galaxy.bytes(star.galaxy) ++ Point3.bytes(Parsecs)(star.loc)
  private val gen:Star => Generator = BurtleRot2.fromBytes _ compose bytes

  trait Instances {
    implicit val starHasEq:Eq[Star] = Eq.fromUniversalEquals[Star]
    implicit val starHasDist:Dist[Star] =
      for {
        galaxy <- Dist[Galaxy]
        interval = Interval(-galaxy.radius, galaxy.radius)
        dist = interval.dist(-galaxy.radius, galaxy.radius, galaxy.radius / 1000)
        x <- dist
        y <- dist
        z <- dist
      } yield Star(galaxy, Point3(x, y, z))

    implicit val starHasSparseSpace:SparseSpace3[Star, Planet] =
      SparseSpace3.fromStandardProof[Star, Planet](AstronomicalUnits, AstronomicalUnits(1))(Planet.apply)(bytes)
  }
  object instances extends Instances
}
