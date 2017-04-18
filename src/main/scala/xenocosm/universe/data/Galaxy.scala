package xenocosm
package universe
package data

import java.nio.ByteBuffer

import cats.{Eq, Show}
import squants.energy._
import squants.mass._
import squants.space._
import spire.random.Dist
import spire.random.rng.BurtleRot2
import squants.thermal.{Kelvin, Temperature}

import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._

import HubbleSequence.instances._
import MorganKeenan.instances._

final case class Galaxy(
  universe:Universe,
  loc:Point3,
  hubbleSequence:HubbleSequence,
  mass:Mass,
  luminosity:Power,
  diameter:Length,
  temperature:Temperature
)

object Galaxy {
  val MAX_MASS       =  30000000000000L
  val MIN_MASS       =          550000L
  val MAX_DIAMETER   =          800000L
  val MIN_DIAMETER   =           10000L
  val MAX_LUMINOSITY = 350000000000000L
  val MIN_LUMINOSITY =     10000000000L

  def dist(universe:Universe, loc:Point3):Dist[Galaxy] =
    for {
      hubble ← implicitly[Dist[HubbleSequence]]
      mass ← Dist.gen(_.nextLong(MIN_MASS, MAX_MASS))
      luminosity ← Dist.gen(_.nextLong(MIN_LUMINOSITY, MAX_LUMINOSITY))
      diameter ← Dist.gen(_.nextLong(MIN_DIAMETER, MAX_DIAMETER))
      mk ← implicitly[Dist[MorganKeenan]]
      int = MorganKeenan.temperatureRangeOf(mk)
      temperature ← int.dist(int.lower, int.upper, Kelvin(1))
    } yield Galaxy(universe, loc, hubble, SolarMasses(mass), SolarLuminosities(luminosity), Parsecs(diameter), temperature)

  def star(seed:Long, galaxy:Galaxy, loc:Point3):Option[Star] = {
    val buffer:ByteBuffer =
      ByteBuffer.
        allocate(48).
        putLong(seed).
        putLong(galaxy.universe.uuid.getMostSignificantBits).
        putLong(galaxy.universe.uuid.getLeastSignificantBits).
        putDouble(galaxy.loc.x.toParsecs).
        putDouble(galaxy.loc.y.toParsecs).
        putDouble(galaxy.loc.z.toParsecs).
        putDouble(loc.x.toParsecs).
        putDouble(loc.y.toParsecs).
        putDouble(loc.z.toParsecs)

    proof(buffer.array()) map { bytes ⇒
      val a:Int = ByteBuffer.wrap(Array[Byte](bytes.take(4):_*)).getInt
      val b:Int = ByteBuffer.wrap(Array[Byte](bytes.slice(4, 4):_*)).getInt
      val c:Int = ByteBuffer.wrap(Array[Byte](bytes.slice(8, 4):_*)).getInt
      val d:Int = ByteBuffer.wrap(Array[Byte](bytes.slice(12, 4):_*)).getInt
      Star.dist(galaxy, loc)(BurtleRot2.create(a, b, c, d))
    }
  }

  trait Instances {
    import cats.syntax.show._
    import HubbleSequence.instances._

    implicit val galaxyHasShow:Show[Galaxy] = Show.show {
      (a:Galaxy) ⇒
        """%s (Hubble Sequence: %s)
          |Mass: %s
          |Luminosity: %s
          |Diameter: %s
          |Mean Color Temperature: %s
          |""".stripMargin.format(
          "Unnamed Galaxy",
          a.hubbleSequence.show,
          a.mass.toString(SolarMasses),
          a.luminosity.toString(SolarLuminosities),
          a.diameter.toString(Parsecs),
          a.temperature.toString(Kelvin)
        )
    }

    implicit val galaxyHasEq:Eq[Galaxy] = Eq.fromUniversalEquals[Galaxy]
  }

  object instances extends Instances
}
