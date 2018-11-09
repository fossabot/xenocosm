package galaxique
package data

import cats.Eq
import spire.math.Interval
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy.{Power, SolarLuminosities}
import squants.mass.{Mass, SolarMasses}
import squants.space.{Length, MegaParsecs, Parsecs, SolarRadii}

final case class Galaxy(universe:Universe, loc:Point3) { self =>
  private val gen:Generator = Galaxy.gen(self)
  val luminosity:Power = Galaxy.luminosityDist(gen).in(SolarLuminosities)
  val diameter:Length = Galaxy.diameterDist(gen).in(SolarRadii)
  val mass:Mass = Galaxy.massDist(gen).in(SolarMasses)
  lazy val radius:Length = diameter / 2
}

object Galaxy {
  import interop.squants.instances._

  lazy val scale:Length = Parsecs(1)

  private[data] val bytes:Galaxy => Array[Byte] = galaxy =>
    Universe.bytes(galaxy.universe) ++ Point3.bytes(Parsecs)(galaxy.loc)
  private val gen:Galaxy => Generator = BurtleRot2.fromBytes _ compose bytes

  private lazy val luminosityMin:Power = SolarLuminosities(10000000000L)
  private lazy val luminosityMax:Power = SolarLuminosities(350000000000000L)
  private lazy val luminosity:Interval[Power] = Interval(luminosityMin, luminosityMax)
  private lazy val luminosityDist:Dist[Power] = luminosity.dist(luminosityMin, luminosityMax, luminosityMin / 100)

  private lazy val diameterMin:Length = Universe.scale
  private lazy val diameterMax:Length = MegaParsecs(1)
  private lazy val diameter:Interval[Length] = Interval(diameterMin, diameterMax)
  private lazy val diameterDist:Dist[Length] = diameter.dist(diameterMin, diameterMax, diameterMin / 100)

  private lazy val massMin:Mass = SolarMasses(550000L)
  private lazy val massMax:Mass = SolarMasses(30000000000000L)
  private lazy val mass:Interval[Mass] = Interval(massMin, massMax)
  private lazy val massDist:Dist[Mass] = mass.dist(massMin, massMax, massMin / 100)

  // Scale a double from [0.0, 1.0) to correspond to a point within the galaxy
  private val toCoordinate:Galaxy => Double => Length = galaxy => d =>
    scale * ((galaxy.diameter * ((2 * d) - 1)) / scale).floor

  //FIXME: Calculate z-axis
  val point:Galaxy => Dist[Point3] = galaxy =>
    for {
      x <- Dist.double
      y <- Dist.double
    } yield Point3(
      toCoordinate(galaxy)(x),
      toCoordinate(galaxy)(y),
      Parsecs(0)
    )

  trait Instances {
    implicit val galaxyHasEq:Eq[Galaxy] = Eq.fromUniversalEquals[Galaxy]

    implicit val galaxyHasSparseSpace:SparseSpace3[Galaxy, Star] =
      SparseSpace3.fromStandardProof[Galaxy, Star](Parsecs, Galaxy.scale)(Star.apply)(bytes)
  }

  object instances extends Instances
}
