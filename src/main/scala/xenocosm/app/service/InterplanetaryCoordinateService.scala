package xenocosm
package app
package service

import org.http4s._
import org.http4s.dsl._
import squants.space.Length

import xenocosm.geometry.data.Point3
import xenocosm.geometry.syntax._
import xenocosm.interop.instances._
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

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) / ♉(locU) / ♊(locG) / ♋(locS) ⇒
      (for {
        galaxy ← universe.locate(locU)
        star ← galaxy.locate(locG)
        ssb ← star.locate(locS)
      } yield ssb) match {
        case Some(planet:Planet) ⇒
          Ok(screen.InterplanetarySpace(planet)).
            putHeaders(scaleHeader)
        case Some(dwarfPlanet:DwarfPlanet) ⇒
          Ok(screen.InterplanetarySpace(dwarfPlanet)).
            putHeaders(scaleHeader)
        case Some(smallBody:SmallBody) ⇒
          Ok(screen.InterplanetarySpace(smallBody)).
            putHeaders(scaleHeader)
        case None ⇒
          Ok(screen.InterplanetarySpace.apply).
            putHeaders(scaleHeader).
            putHeaders(nearbyLocations(req, Star(Galaxy(universe, locU), locG), locS):_*)
      }
  }
}
