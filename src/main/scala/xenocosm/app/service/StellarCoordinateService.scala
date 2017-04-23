package xenocosm
package app
package service

import java.util.UUID
import cats.syntax.show._
import org.http4s._
import org.http4s.dsl._
import squants.UnitOfMeasure
import squants.energy.SolarLuminosities
import squants.mass.SolarMasses
import squants.space.{Length, Parsecs, SolarRadii}
import squants.thermal.Kelvin

import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._
import xenocosm.universe.data._

import MorganKeenan.instances._

object StellarCoordinateService extends CoordinateService[Star, Galaxy] {

  val scale:Length = Parsecs(1)
  val scaleUOM:UnitOfMeasure[Length] = Parsecs

  def show(star:Star):String =
    """A Star
      |  Morgan-Keenan: %s
      |  Mass: %s
      |  Luminosity: %s
      |  Radius: %s
      |  Temperature: %s
      |""".stripMargin.format(
      star.morganKeenan.show,
      star.mass.toString(SolarMasses),
      star.luminosity.toString(SolarLuminosities),
      star.radius.toString(SolarRadii),
      star.temperature.toString(Kelvin)
    )

  def screen(star:Star):fansi.Str =
    show(star).
      split("\n").
      map(_.zipWithIndex.map({ case (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c") }).mkString).
      mkString("\n")

  def path(galaxy:Galaxy)(loc:Point3):Uri.Path =
    GalacticCoordinateService.path(galaxy.universe)(galaxy.loc).concat(path(loc))

  def nearby(galaxy:Galaxy, loc:Point3):Iterator[Point3] =
    Point3.
      wholePointsInCube(scale * 2, scale, loc).
      flatMap(xenocosm.universe.proof(galaxy, _))

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / uuid / locGalactic / locStellar ⇒
      (getSeed(req), uuid, locGalactic, locStellar) match {
        case (_, UniverseService.regex(uuidStr), regex(x1, y1, z1), regex(x2, y2, z2)) ⇒
          val universe = Universe(UUID.fromString(uuidStr))
          val locGalactic = GalacticCoordinateService.scaled(x1, y1, z1)
          val galaxy = Galaxy(universe, locGalactic)
          val locStellar = scaled(x2, y2, z2)
          (for {
            _ ← xenocosm.universe.proof(universe, locGalactic)
            b ← xenocosm.universe.proof(galaxy, locStellar)
          } yield b) match {
            case Some(_) ⇒
              val star = Star(galaxy, locStellar)
              Ok(screen(star)).putHeaders(scaleHeader)
            case None ⇒
              Ok("You are in interstellar space. The stars splay out before you like a painting by some Power.").
                putHeaders(scaleHeader).
                putHeaders(nearbyLocations(req, galaxy, locStellar):_*)
          }
        case _ ⇒ NotFound()
      }
  }
}
