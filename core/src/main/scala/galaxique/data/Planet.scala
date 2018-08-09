package galaxique
package data

import cats.Eq
import spire.math.Interval
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.mass.{Density, Kilograms, Mass, SolarMasses}
import squants.space._
import squants.time.{Seconds, Time}

final case class Planet(star:Star, loc:Point3) { self =>
  private val gen:Generator = Planet.gen(self)
  val radius:Length = Planet.radiusDist(gen).in(Kilometers)
  val mass:Mass = Planet.massDist(gen).in(Kilograms)
  val semiMajorAxis:Length = Planet.semiMajorAxisDist(gen).in(AstronomicalUnits)
  lazy val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  lazy val density:Density = mass / volume
  lazy val orbitalPeriod:Time = Seconds(Math.sqrt(semiMajorAxis.cubed.to(CubicMeters) / star.μ) * Math.PI * 2)
}

object Planet {
  import interop.squants.instances._
  import Star.instances._

  private[data] val bytes:Planet => Array[Byte] = planet =>
    Star.bytes(planet.star) ++ Point3.bytes(AstronomicalUnits)(planet.loc)
  private val gen:Planet => Generator = BurtleRot2.fromBytes _ compose bytes

  // https://en.wikipedia.org/wiki/List_of_largest_exoplanets
  // https://en.wikipedia.org/wiki/Mercury_(planet)
  private lazy val radiusMin:Length = Kilometers(2440)
  private lazy val radiusMax:Length = SolarRadii(0.07087)
  private lazy val radius:Interval[Length] = Interval(radiusMin, radiusMax)
  private lazy val radiusDist:Dist[Length] = radius.dist(radiusMin, radiusMax, radiusMin / 10)

  // https://en.wikipedia.org/wiki/List_of_most_massive_exoplanets
  // https://en.wikipedia.org/wiki/Mercury_(planet)
  private lazy val massMin:Mass = Kilograms(3.3011e23)
  private lazy val massMax:Mass = SolarMasses(0.01)
  private lazy val mass:Interval[Mass] = Interval(massMin, massMax)
  private lazy val massDist:Dist[Mass] = mass.dist(massMin, massMax, massMin / 10)

  // https://www.nasa.gov/home/hqnews/2004/mar/HQ_04091_sedna_discovered.html
  private lazy val semiMajorAxisMin:Length = AstronomicalUnits(0.1)
  private lazy val semiMajorAxisMax:Length = AstronomicalUnits(900)
  private lazy val semiMajorAxis:Interval[Length] = Interval(semiMajorAxisMin, semiMajorAxisMax)
  private lazy val semiMajorAxisDist:Dist[Length] = semiMajorAxis.dist(semiMajorAxisMin, semiMajorAxisMax, semiMajorAxisMin)

  trait Instances {
    implicit val planetHasEq:Eq[Planet] = Eq.fromUniversalEquals[Planet]
    implicit val planetHasDist:Dist[Planet] =
      for {
        star <- Dist[Star]
        radius <- radiusDist
        mass <- massDist
        volume = (radius.cubed * Math.PI * 4) / 3
        density = mass / volume
        rocheLimit = star.rocheLimit(density)
        interval = Interval(rocheLimit, AstronomicalUnits(100))
        dist = interval.dist(rocheLimit, AstronomicalUnits(100), rocheLimit / 10)
        x0 <- dist
        y0 <- dist
        z0 <- dist
        x = x0.in(AstronomicalUnits).floor
        y = y0.in(AstronomicalUnits).floor
        z = z0.in(AstronomicalUnits).floor
      } yield Planet(star, Point3(x, y, z))
  }
  object instances extends Instances
}
