package xenocosm
package app
package screen

import cats.syntax.show._
import squants.energy.SolarLuminosities
import squants.space.Parsecs
import squants.thermal.Kelvin

import xenocosm.phonology.syntax._
import xenocosm.universe.data.Galaxy
import xenocosm.universe.instances._

object IntergalacticSpace {

  def show(galaxy:Galaxy):String =
    """The %s Galaxy
      |  Hubble Sequence: %s
      |  Luminosity: %s
      |  Diameter: %s
      |  Mean Temperature: %s
      |  Galactic Common: %s
      |""".stripMargin.format(
      galaxy.phonology.translate("galaxy").romanize.capitalize,
      galaxy.hubbleSequence.show,
      galaxy.luminosity.toString(SolarLuminosities, "%e"),
      galaxy.diameter.toString(Parsecs, "%e"),
      galaxy.temperature.toString(Kelvin, "%e"),
      galaxy.phonology.translate("language").romanize.capitalize
    )

  def show:String = "You are in intergalactic space."

  // scalastyle:off magic.number
  def apply:fansi.Str =
    fansi.Color.True(32, 255 - 32, 255)(show)

  def apply(galaxy:Galaxy):fansi.Str =
    fansi.Color.True(32, 255 - 32, 255)(show(galaxy))
  // scalastyle:on magic.number
}
