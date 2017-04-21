package xenocosm
package universe
package data

import java.security.MessageDigest
import cats.Eq
import squants.energy._
import squants.space._
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.thermal.{Kelvin, Temperature}

import xenocosm.app.config
import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._

import HubbleSequence.instances._
import MorganKeenan.instances._

final case class Galaxy(universe:Universe, loc:Point3) {
  private def bytes:Array[Byte] = universe.digest ++ loc.digest(Parsecs)
  val digest:Array[Byte] = MessageDigest.getInstance("MD5").digest(bytes)
  private val gen:Generator = BurtleRot2.fromBytes(digest)

  val hubbleSequence:HubbleSequence = implicitly[Dist[HubbleSequence]].apply(gen)
  val luminosity:Power = config.galaxy.luminosity.dist(SolarLuminosities(1))(gen)
  val diameter:Length = config.galaxy.diameter.dist(Parsecs(1))(gen)
  val temperature:Temperature = Galaxy.galacticMeanTemperature(gen)
}

object Galaxy {

  val galacticMeanTemperature:Dist[Temperature] =
    for {
      mk ← implicitly[Dist[MorganKeenan]]
      range = MorganKeenan.temperatureRangeOf(mk)
      temperature ← range.dist(range.lower, range.upper, Kelvin(1))
    } yield temperature

  trait Instances {
    implicit val galaxyHasEq:Eq[Galaxy] = Eq.fromUniversalEquals[Galaxy]
  }

  object instances extends Instances
}
