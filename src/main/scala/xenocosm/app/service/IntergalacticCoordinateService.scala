package xenocosm
package app
package service

import org.http4s._
import org.http4s.dsl._
import squants.space.Length

import xenocosm.chapbook.GalacticForge
import xenocosm.geometry.data.Point3
import xenocosm.geometry.syntax._
import xenocosm.interop.instances._
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

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) / ♉(locU) ⇒
      universe.locate(locU) match {
        case Some(galaxy) ⇒
          Ok(screen.IntergalacticSpace(galaxy)).
            putHeaders(scaleHeader).
            putHeaders(discover(req, universe, locU))
        case _ ⇒
          val stanza = GalacticForge.fromIntergalacticSpace((universe, locU))
          Ok(screen.IntergalacticSpace(stanza)).
            putHeaders(scaleHeader).
            putHeaders(nearbyLocations(req, universe, locU):_*)
      }
  }
}
