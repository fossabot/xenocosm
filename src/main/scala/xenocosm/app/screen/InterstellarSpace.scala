package xenocosm
package app
package screen

import cats.syntax.show._
import squants.energy.SolarLuminosities
import squants.mass.SolarMasses
import squants.space.SolarRadii
import squants.thermal.Kelvin

import xenocosm.phonology.syntax._
import xenocosm.universe.data.Star
import xenocosm.universe.instances._

object InterstellarSpace {

  val show:Star ⇒ String = star ⇒
    """The %s System
      |  Morgan-Keenan: %s
      |  Mass: %s
      |  Luminosity: %s
      |  Radius: %s
      |  Temperature: %s
      |  μ: %e m³/s²
      |  System Common: %s
      |""".stripMargin.format(
      star.phonology.translate("star").romanize.capitalize,
      star.morganKeenan.show,
      star.mass.toString(SolarMasses, "%e"),
      star.luminosity.toString(SolarLuminosities, "%e"),
      star.radius.toString(SolarRadii, "%e"),
      star.temperature.toString(Kelvin, "%e"),
      star.μ,
      star.phonology.translate("language").romanize.capitalize
    )

  // scalastyle:off magic.number
  def apply:fansi.Str =
    fansi.Color.True(64, 255 - 64, 255) {
      """You are in interstellar space.
        |The stars splay out before you like a painting by some Power.
        |""".stripMargin
    }

  def apply(star:Star):fansi.Str =
    fansi.Color.True(64, 255 - 64, 255)(show(star))
  // scalastyle:on magic.number
}
