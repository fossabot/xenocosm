package xenocosm.app.screen

import squants.mass.{Kilograms, KilogramsPerCubicMeter}
import squants.space.{AstronomicalUnits, CubicMeters, Kilometers}
import squants.time.Days

import xenocosm.phonology.Romanization
import xenocosm.phonology.syntax._
import xenocosm.universe.data.{DwarfPlanet, Planet, SmallBody}

object InterplanetarySpace {
  private implicit val romanization = Romanization.default

  def show(planet:Planet): String =
    """%s (Planet)
      |  Radius: %s
      |  Mass: %s
      |  Volume: %s
      |  Density: %s
      |  Semi-Major Axis: %s
      |  Semi-Minor Axis: %s
      |  Orbital Period: %s
      |""".stripMargin.format(
      planet.star.phonology.translate(planet.loc.toString).romanize.capitalize,
      planet.radius.toString(Kilometers),
      planet.mass.toString(Kilograms, "%e"),
      planet.volume.toString(CubicMeters, "%e"),
      planet.density.toString(KilogramsPerCubicMeter, "%e"),
      planet.semiMajorAxis.toString(AstronomicalUnits, "%e"),
      planet.semiMinorAxis.toString(AstronomicalUnits, "%e"),
      planet.orbitalPeriod.toString(Days, "%e")
    )

  def show(dwarfPlanet:DwarfPlanet): String =
    """%s (Dwarf Planet)
      |  Radius: %s
      |  Mass: %s
      |  Volume: %s
      |  Density: %s
      |  Semi-Major Axis: %s
      |  Semi-Minor Axis: %s
      |  Orbital Period: %s
      |""".stripMargin.format(
      dwarfPlanet.star.phonology.translate(dwarfPlanet.loc.toString).romanize.capitalize,
      dwarfPlanet.radius.toString(Kilometers),
      dwarfPlanet.mass.toString(Kilograms, "%e"),
      dwarfPlanet.volume.toString(CubicMeters, "%e"),
      dwarfPlanet.density.toString(KilogramsPerCubicMeter, "%e"),
      dwarfPlanet.semiMajorAxis.toString(AstronomicalUnits, "%e"),
      dwarfPlanet.semiMinorAxis.toString(AstronomicalUnits, "%e"),
      dwarfPlanet.orbitalPeriod.toString(Days, "%e")
    )

  def show(smallBody:SmallBody): String =
    """%s (Small Body)
      |  Mass: %s
      |  Volume: %s
      |  Density: %s
      |  Semi-Major Axis: %s
      |  Semi-Minor Axis: %s
      |  Orbital Period: %s
      |""".stripMargin.format(
      smallBody.star.phonology.translate(smallBody.loc.toString).romanize.capitalize,
      smallBody.mass.toString(Kilograms, "%e"),
      smallBody.volume.toString(CubicMeters, "%e"),
      smallBody.density.toString(KilogramsPerCubicMeter, "%e"),
      smallBody.semiMajorAxis.toString(AstronomicalUnits, "%e"),
      smallBody.semiMinorAxis.toString(AstronomicalUnits, "%e"),
      smallBody.orbitalPeriod.toString(Days, "%e")
    )

  // scalastyle:off magic.number
  def apply:fansi.Str =
    fansi.Color.True(128, 128, 255) {
      """You are in interplanetary space.
        |The stellar system objects splay out before you like a painting by some Power.
        |""".stripMargin
    }

  def apply(planet:Planet):fansi.Str =
    fansi.Color.True(128, 128, 255)(show(planet))

  def apply(dwarfPlanet:DwarfPlanet):fansi.Str =
    fansi.Color.True(128, 128, 255)(show(dwarfPlanet))

  def apply(smallBody:SmallBody):fansi.Str =
    fansi.Color.True(128, 128, 255)(show(smallBody))
  // scalastyle:on magic.number
}
