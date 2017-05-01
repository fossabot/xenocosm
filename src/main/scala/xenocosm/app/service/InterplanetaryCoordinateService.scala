package xenocosm
package app
package service

import org.http4s._
import org.http4s.dsl._
import squants.mass.{Kilograms, KilogramsPerCubicMeter}
import squants.space.{AstronomicalUnits, CubicMeters, Kilometers, Length}
import squants.time.Days

import xenocosm.geometry.data.Point3
import xenocosm.geometry.syntax._
import xenocosm.interop.instances._
import xenocosm.phonology.syntax._
import xenocosm.universe.data._
import xenocosm.universe.instances._

object InterplanetaryCoordinateService extends CoordinateService[Star, StellarSystemBody] {

  val scale:Length = app.config.services.interplanetaryCoordinates.scale

  def path(star:Star)(loc:Point3):Uri.Path =
    InterstellarCoordinateService.path(star).concat(path(loc))

  def nearby(star:Star, loc:Point3):Iterator[Point3] =
    Point3.
      wholePointsInCube(scale * 2, scale, loc).
      flatMap(x ⇒ star.locate(x) map (_.loc))

  def screen:fansi.Str =
    fansi.Color.True(128, 128, 255) {
      """You are in interplanetary space.
        |The stellar system objects splay out before you like a painting by some Power.
        |""".stripMargin
    }

  def screen(planet:Planet):fansi.Str =
    fansi.Color.True(128, 128, 255) {
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
    }

  def screen(dwarfPlanet:DwarfPlanet):fansi.Str =
    fansi.Color.True(128, 128, 255) {
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
    }

  def screen(smallBody:SmallBody):fansi.Str =
    fansi.Color.True(128, 128, 255) {
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
    }

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) / ♉(locU) / ♊(locG) / ♋(locS) ⇒
      (for {
        galaxy ← universe.locate(locU)
        star ← galaxy.locate(locG)
        ssb ← star.locate(locS)
      } yield ssb) match {
        case Some(planet:Planet) ⇒
          Ok(screen(planet)).
            putHeaders(scaleHeader)
        case Some(dwarfPlanet:DwarfPlanet) ⇒
          Ok(screen(dwarfPlanet)).
            putHeaders(scaleHeader)
        case Some(smallBody:SmallBody) ⇒
          Ok(screen(smallBody)).
            putHeaders(scaleHeader)
        case None ⇒
          Ok(screen).
            putHeaders(scaleHeader).
            putHeaders(nearbyLocations(req, Star(Galaxy(universe, locU), locG), locS):_*)
      }
  }
}
