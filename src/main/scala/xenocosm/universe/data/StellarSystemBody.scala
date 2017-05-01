package xenocosm
package universe
package data

import cats.Eq
import spire.random.Generator
import spire.random.rng.BurtleRot2
import squants.mass.{Density, Mass}
import squants.space._
import squants.time.{Seconds, Time}

import xenocosm.geometry.data.Point3

sealed trait StellarSystemBody {
  val star:Star
  val loc:Point3
}

final case class Planet(star:Star, loc:Point3) extends StellarSystemBody { self ⇒
  private val gen:Generator = StellarSystemBody.gen(self)
  val radius:Length = app.config.stellarSystemBody.planetRadius.dist(gen)
  val mass:Mass = app.config.stellarSystemBody.planetMass.dist(gen)
  val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  val density:Density = mass / volume
  val rocheLimit:Length = Math.pow(star.density / density, 1d / 3d) * 1.26 * star.radius
  val semiMajorAxis:Length = app.config.stellarSystemBody.semiMajorAxis.dist(gen) + rocheLimit
  val semiMinorAxis:Length = SolarRadii(gen.nextDouble(rocheLimit.toSolarRadii, semiMajorAxis.toSolarRadii))
  val orbitalPeriod:Time = Seconds(Math.sqrt(semiMajorAxis.cubed.to(CubicMeters) / star.μ) * Math.PI * 2)
}

final case class DwarfPlanet(star:Star, loc:Point3) extends StellarSystemBody { self ⇒
  private val gen:Generator = StellarSystemBody.gen(self)
  val radius:Length = app.config.stellarSystemBody.planetRadius.dist(gen)
  val mass:Mass = app.config.stellarSystemBody.planetMass.dist(gen)
  val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  val density:Density = mass / volume
  val rocheLimit:Length = Math.pow(star.density / density, 1d / 3d) * 1.26 * star.radius
  val semiMajorAxis:Length = app.config.stellarSystemBody.semiMajorAxis.dist(gen) + rocheLimit
  val semiMinorAxis:Length = SolarRadii(gen.nextDouble(rocheLimit.toSolarRadii, semiMajorAxis.toSolarRadii))
  val orbitalPeriod:Time = Seconds(Math.sqrt(semiMajorAxis.cubed.to(CubicMeters) / star.μ) * Math.PI * 2)
}

final case class SmallBody(star:Star, loc:Point3) extends StellarSystemBody { self ⇒
  private val gen:Generator = StellarSystemBody.gen(self)
  val radius:Length = app.config.stellarSystemBody.planetRadius.dist(gen)
  val mass:Mass = app.config.stellarSystemBody.planetMass.dist(gen)
  val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  val density:Density = mass / volume
  val rocheLimit:Length = Math.pow(star.density / density, 1d / 3d) * 1.26 * star.radius
  val semiMajorAxis:Length = app.config.stellarSystemBody.semiMajorAxis.dist(gen) + rocheLimit
  val semiMinorAxis:Length = SolarRadii(gen.nextDouble(rocheLimit.toSolarRadii, semiMajorAxis.toSolarRadii))
  val orbitalPeriod:Time = Seconds(Math.sqrt(semiMajorAxis.cubed.to(CubicMeters) / star.μ) * Math.PI * 2)
}

object StellarSystemBody {
  val bytes:StellarSystemBody ⇒ Array[Byte] = ssb ⇒
    Star.bytes(ssb.star) ++ Point3.bytes(AstronomicalUnits)(ssb.loc)

  val gen:StellarSystemBody ⇒ Generator = BurtleRot2.fromBytes _ compose Digest.md5 compose bytes

  trait PlanetInstances {
    implicit val planetHasEq:Eq[Planet] = Eq.fromUniversalEquals[Planet]
  }

  trait DwarfPlanetInstances {
    implicit val dwarfPlanetHasEq:Eq[DwarfPlanet] = Eq.fromUniversalEquals[DwarfPlanet]
  }

  trait SmallBodyInstances {
    implicit val smallBodyHasEq:Eq[SmallBody] = Eq.fromUniversalEquals[SmallBody]
  }

  object instances extends PlanetInstances with DwarfPlanetInstances with SmallBodyInstances
}
