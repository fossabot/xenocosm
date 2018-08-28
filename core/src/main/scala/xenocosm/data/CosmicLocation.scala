package xenocosm
package data

import java.util.UUID
import cats.data.EitherT
import cats.kernel.Eq
import galaxique.data._
import spire.random.Dist
import squants.space.{Length, Parsecs}

final case class CosmicLocation(uuid:UUID, locU:Option[Point3], locG:Option[Point3], locS:Option[Point3]) { self =>
  def distance(to:CosmicLocation):Length = CosmicLocation.distance(self, to)
  lazy val universe:Universe = Universe(uuid)
  lazy val galaxy:Option[Galaxy] = CosmicLocation.findGalaxy(self)
  lazy val star:Option[Star] = CosmicLocation.findStar(self)
  lazy val planet:Option[Planet] = CosmicLocation.findPlanet(self)
  lazy val inIntergalacticSpace:Boolean = galaxy.isEmpty
  lazy val inInterstellarSpace:Boolean = star.isEmpty
  lazy val inInterplanetarySpace:Boolean = planet.isEmpty
}

object CosmicLocation {
  import interop.spire.instances._
  import galaxique.SparseSpace3.syntax._
  import Galaxy.instances._
  import Star.instances._
  import Universe.instances._

  val findGalaxy:CosmicLocation => Option[Galaxy] =
    loc => loc.locU.flatMap(loc.universe.locate)

  val findStar:CosmicLocation => Option[Star] = loc =>
    for {
      galaxy <- loc.galaxy
      locG <- loc.locG
      star <- galaxy.locate(locG)
    } yield star

  val findPlanet:CosmicLocation => Option[Planet] = loc =>
    for {
      star <- loc.star
      locS <- loc.locS
      planet <- star.locate(locS)
    } yield planet

  def distance(a:CosmicLocation, b:CosmicLocation):Length =
    if (a.uuid == b.uuid) {
      Point3.distance(a.locU, b.locU) + Point3.distance(a.locG, b.locG) + Point3.distance(a.locS, b.locS)
    } else {
      Parsecs(Double.NaN)
    }

  trait Instances {
    implicit val cosmicLocationHasEq:Eq[CosmicLocation] = Eq.fromUniversalEquals[CosmicLocation]

    implicit val cosmicLocationHasDist:Dist[CosmicLocation] =
      EitherT.right[CosmicLocation](Dist[Universe])
        .semiflatMap(universe => Universe.point(universe).map((universe, _)))
        .subflatMap({ case (universe, locU) =>
          universe
            .nearest(locU, Universe.scale * 5)
            .toRight(CosmicLocation(universe.uuid, Some(locU), None, None))
        })
        .semiflatMap(g => Galaxy.point(g).map((g, _)))
        .subflatMap({ case (galaxy, locG) =>
          galaxy
            .nearest(locG, Galaxy.scale * 5)
            .toRight(CosmicLocation(galaxy.universe.uuid, Some(galaxy.loc), Some(locG), None))
        })
        .semiflatMap(s => Star.point(s).map((s, _)))
        .subflatMap({ case (star, locS) =>
          star
            .nearest(locS, Star.scale * 5)
            .toRight(CosmicLocation(star.galaxy.universe.uuid, Some(star.galaxy.loc), Some(star.loc), Some(locS)))
        }).value.map({
          case Right(planet) => CosmicLocation(
            planet.star.galaxy.universe.uuid,
            Some(planet.star.galaxy.loc),
            Some(planet.star.loc),
            Some(planet.loc)
          )
          case Left(loc) => loc
        })
  }
  object instances extends Instances
}
