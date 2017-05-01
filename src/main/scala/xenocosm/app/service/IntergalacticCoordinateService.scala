package xenocosm
package app
package service

import cats.syntax.show._
import org.http4s._
import org.http4s.dsl._
import squants.space.{Length, Parsecs}
import squants.energy.SolarLuminosities
import squants.thermal.Kelvin

import xenocosm.geometry.data.Point3
import xenocosm.geometry.syntax._
import xenocosm.interop.instances._
import xenocosm.phonology.syntax._
import xenocosm.universe.data._
import xenocosm.universe.instances._

object IntergalacticCoordinateService extends CoordinateService[Universe, Galaxy] {

  val scale:Length = app.config.services.intergalacticCoordinates.scale

  def path(universe:Universe)(loc:Point3):Uri.Path =
    UniverseService.path(universe) ++ path(loc)

  val path:Galaxy ⇒ Uri.Path = galaxy ⇒ path(galaxy.universe)(galaxy.loc)

  def nearby(universe:Universe, loc:Point3):Iterator[Point3] =
    Point3.
      wholePointsInCube(scale * 2, scale, loc).
      flatMap(x ⇒ universe.locate(x) map (_.loc))

  def discover(req:Request, universe:Universe, loc:Point3):headers.Location =
    headers.Location(req.uri.withPath(path(universe)(loc) ++ "/0,0,0"))

  private def screen:fansi.Str =
    fansi.Color.True(32, 255 - 32, 255) {
      """You are in intergalactic space.
        |The galaxies splay out before you like a painting by some Power.
        |""".stripMargin
    }

  private def screen(galaxy:Galaxy):fansi.Str =
    fansi.Color.True(32, 255 - 32, 255) {
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
    }

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) / ♉(locU) ⇒
      universe.locate(locU) match {
        case Some(galaxy) ⇒
          Ok(screen(galaxy)).
            putHeaders(scaleHeader).
            putHeaders(discover(req, universe, locU))
        case _ ⇒
          Ok(screen).
            putHeaders(scaleHeader).
            putHeaders(nearbyLocations(req, universe, locU):_*)
      }
  }
}
