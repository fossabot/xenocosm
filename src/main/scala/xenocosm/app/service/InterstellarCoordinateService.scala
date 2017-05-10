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

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) / ♉(locU) / ♊(locG) ⇒
      (for {
        galaxy ← universe.locate(locU)
        star ← galaxy.locate(locG)
      } yield star) match {
        case Some(star) ⇒
          Ok(screen.InterstellarSpace(star)).
            putHeaders(scaleHeader).
            putHeaders(discover(req, star.galaxy, locG))
        case _ ⇒
          Ok(screen.InterstellarSpace.apply).
            putHeaders(scaleHeader).
            putHeaders(nearbyLocations(req, Galaxy(universe, locU), locG):_*)
      }
  }
}
