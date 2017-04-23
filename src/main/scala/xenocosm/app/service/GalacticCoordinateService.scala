package xenocosm
package app
package service

import java.util.UUID
import cats.syntax.show._
import org.http4s._
import org.http4s.dsl._
import squants.UnitOfMeasure
import squants.space.{Length, Parsecs}
import squants.energy.SolarLuminosities
import squants.thermal.Kelvin

import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._
import xenocosm.universe.data._

import HubbleSequence.instances._

object GalacticCoordinateService extends CoordinateService[Galaxy, Universe] {

  val scale:Length = Parsecs(10000)
  val scaleUOM:UnitOfMeasure[Length] = Parsecs

  def show(galaxy:Galaxy):String =
    """A Galaxy
      |  Hubble Sequence: %s
      |  Luminosity: %s
      |  Diameter: %s
      |  Mean Temperature: %s
      |""".stripMargin.format(
      galaxy.hubbleSequence.show,
      galaxy.luminosity.toString(SolarLuminosities, "%e"),
      galaxy.diameter.toString(Parsecs, "%e"),
      galaxy.temperature.toString(Kelvin, "%e")
    )

  def screen(galaxy:Galaxy):fansi.Str =
    show(galaxy).
      split("\n").
      map(_.zipWithIndex.map({ case (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c") }).mkString).
      mkString("\n")

  def path(universe:Universe)(loc:Point3):Uri.Path =
    new Uri.Path(s"/multiverse/${universe.uuid.toString}").concat(path(loc))

  def nearby(universe:Universe, loc:Point3):Iterator[Point3] =
    Point3.
      wholePointsInCube(scale * 2, scale, loc).
      flatMap(xenocosm.universe.proof(universe, _))

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / uuid / loc ⇒
      (getSeed(req), uuid, loc) match {
        case (_, UniverseService.regex(uuidStr), regex(x, y, z)) ⇒
          val universe = Universe(UUID.fromString(uuidStr))
          val loc = scaled(x, y, z)
          xenocosm.universe.proof(universe, loc) match {
            case Some(_) ⇒
              val galaxy = Galaxy(universe, loc)
              Ok(screen(galaxy)).
                putHeaders(scaleHeader, headers.Location(req.uri / "0,0,0"))
            case None ⇒
              Ok("You are in intergalactic space. The galaxies splay out before you like a painting by some Power.").
                putHeaders(scaleHeader).
                putHeaders(nearbyLocations(req, universe, loc):_*)
          }
        case _ ⇒ NotFound()
      }
  }
}
