package xenocosm
package app
package service

import cats.syntax.show._
import org.http4s._
import org.http4s.dsl._
import squants.energy.SolarLuminosities
import squants.mass.SolarMasses
import squants.space.{Length, SolarRadii}
import squants.thermal.Kelvin

import xenocosm.geometry.data.Point3
import xenocosm.geometry.syntax._
import xenocosm.interop.instances._
import xenocosm.phonology.syntax._
import xenocosm.universe.data._
import xenocosm.universe.instances._

object InterstellarCoordinateService extends CoordinateService[Galaxy, Star] {

  val scale:Length = app.config.services.interstellarCoordinates.scale

  def path(galaxy:Galaxy)(loc:Point3):Uri.Path =
    IntergalacticCoordinateService.path(galaxy).concat(path(loc))

  val path:Star ⇒ Uri.Path = star ⇒ path(star.galaxy)(star.loc)

  def nearby(galaxy:Galaxy, loc:Point3):Iterator[Point3] =
    Point3.
      wholePointsInCube(scale * 2, scale, loc).
      flatMap(x ⇒ galaxy.locate(x) map (_.loc))

  def discover(req:Request, galaxy:Galaxy, loc:Point3):headers.Location =
    headers.Location(req.uri.withPath(path(galaxy)(loc) ++ "/0,0,0"))

  // scalastyle:off magic.number
  private def screen:fansi.Str =
    fansi.Color.True(64, 255 - 64, 255) {
      """You are in interstellar space.
        |The stars splay out before you like a painting by some Power.
        |""".stripMargin
    }

  private def screen(star:Star):fansi.Str =
    fansi.Color.True(64, 255 - 64, 255) {
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
    }
  // scalastyle:on magic.number

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) / ♉(locU) / ♊(locG) ⇒
      (for {
        galaxy ← universe.locate(locU)
        star ← galaxy.locate(locG)
      } yield star) match {
        case Some(star) ⇒
          Ok(screen(star)).
            putHeaders(scaleHeader).
            putHeaders(discover(req, star.galaxy, locG))
        case _ ⇒
          Ok(screen).
            putHeaders(scaleHeader).
            putHeaders(nearbyLocations(req, Galaxy(universe, locU), locG):_*)
      }
  }
}
