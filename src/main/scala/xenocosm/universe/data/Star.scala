package xenocosm
package universe
package data

import cats.{Eq, Show}
import spire.random.Dist
import squants.energy._
import squants.mass._
import squants.space._
import squants.thermal.{Kelvin, Temperature}
import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._
import MorganKeenan.instances._

final case class Star(
  galaxy:Galaxy,
  loc:Point3,
  mk:MorganKeenan,
  mass:Mass,
  luminosity:Power,
  radius:Length,
  temperature:Temperature
)

object Star {

  def dist(galaxy:Galaxy, loc:Point3):Dist[Star] =
    for {
      mk ← implicitly[Dist[MorganKeenan]]
      massRange = MorganKeenan.massRangeOf(mk)
      powerRange = MorganKeenan.powerRangeOf(mk)
      radiusRange = MorganKeenan.radiusRangeOf(mk)
      temperatureRange = MorganKeenan.temperatureRangeOf(mk)
      mass ← massRange.dist(massRange.lower, massRange.upper, SolarMasses(1))
      luminosity ← powerRange.dist(powerRange.lower, powerRange.upper, SolarLuminosities(1))
      radius ← radiusRange.dist(radiusRange.lower, radiusRange.upper, SolarRadii(1))
      temperature ← temperatureRange.dist(temperatureRange.lower, temperatureRange.upper, Kelvin(1))
    } yield Star(galaxy, loc, mk, mass, luminosity, radius, temperature)

  trait Instances {
    import cats.syntax.show._
    import data.MorganKeenan.instances._

    implicit val starHasShow:Show[Star] = Show.show {
      (a:Star) ⇒
        """%s (Morgan-Keenan: %s)
          |Mass: %s
          |Luminosity: %s
          |Radius: %s
          |Mean Color Temperature: %s
          |""".stripMargin.format(
          "Unnamed Star",
          a.mk.show,
          a.mass.toString(SolarMasses),
          a.luminosity.toString(SolarLuminosities),
          a.radius.toString(SolarRadii),
          a.temperature.toString(Kelvin)
        )
    }
    implicit val starHasEq:Eq[Star] = Eq.fromUniversalEquals[Star]
  }

  object instances extends Instances
}
