package galaxique
package data

import cats.Eq
import spire.math.Interval
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy.{Power, SolarLuminosities}
import squants.mass.{Mass, SolarMasses}
import squants.space.{Length, Parsecs}

final case class Galaxy(universe:Universe, loc:Point3) { self =>
  private val gen:Generator = Galaxy.gen(self)
  val luminosity:Power = Galaxy.luminosityDist(gen)
  val diameter:Length = Galaxy.diameterDist(gen)
  val mass:Mass = Galaxy.massDist(gen)
  lazy val radius:Length = diameter / 2
}

object Galaxy {
  import interop.length._
  import interop.mass._
  import interop.power._
  import Universe.instances._

  private[data] val bytes:Galaxy => Array[Byte] = galaxy =>
    Universe.bytes(galaxy.universe) ++ Point3.bytes(Parsecs)(galaxy.loc)
  private val gen:Galaxy => Generator = BurtleRot2.fromBytes _ compose bytes

  private lazy val luminosityMin:Power = SolarLuminosities(10000000000L)
  private lazy val luminosityMax:Power = SolarLuminosities(350000000000000L)
  private lazy val luminosity:Interval[Power] = Interval(luminosityMin, luminosityMax)
  private lazy val luminosityDist:Dist[Power] = luminosity.dist(luminosityMin, luminosityMax, luminosityMin / 100)

  private lazy val diameterMin:Length = Parsecs(10000)
  private lazy val diameterMax:Length = Parsecs(800000)
  private lazy val diameter:Interval[Length] = Interval(diameterMin, diameterMax)
  private lazy val diameterDist:Dist[Length] = diameter.dist(diameterMin, diameterMax, diameterMin / 100)

  private lazy val massMin:Mass = SolarMasses(550000L)
  private lazy val massMax:Mass = SolarMasses(30000000000000L)
  private lazy val mass:Interval[Mass] = Interval(massMin, massMax)
  private lazy val massDist:Dist[Mass] = mass.dist(massMin, massMax, massMin / 100)

  trait Instances {
    implicit val galaxyHasEq:Eq[Galaxy] = Eq.fromUniversalEquals[Galaxy]
    implicit val galaxyHasDist:Dist[Galaxy] =
      for {
        universe <- Dist[Universe]
        interval = Interval(-universe.radius, universe.radius)
        dist = interval.dist(-universe.radius, universe.radius, universe.radius / 1000)
        x <- dist
        y <- dist
        z <- dist
      } yield Galaxy(universe, Point3(x, y, z))

    implicit val galaxyHasSparseSpace:SparseSpace3[Galaxy, Star] =
      SparseSpace3.fromStandardProof[Galaxy, Star](Parsecs, Parsecs(1))(Star.apply)(bytes)
  }

  object instances extends Instances
}
