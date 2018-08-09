package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import galaxique.data._
import spire.random.Dist
import squants.space.{Length, Parsecs}

final case class CosmicLocation(uuid:UUID, locU:Option[Point3], locG:Option[Point3], locS:Option[Point3]) { self =>
  def distance(to:CosmicLocation):Length = CosmicLocation.distance(self, to)
}

object CosmicLocation {
  import spire.syntax.metricSpace._
  import galaxique.SparseSpace3.syntax._
  import galaxique.data.Point3.instances._
  import Galaxy.instances._
  import Planet.instances._
  import Star.instances._
  import Universe.instances._

  private def distance(a:Option[Point3], b:Option[Point3]):Length =
    (a, b) match {
      case (Some(locA), Some(locB)) => locA distance locB
      case (None, Some(locB)) => Point3.zero distance locB
      case (Some(locA), None) => Point3.zero distance locA
      case _ => Point3.zero.x
    }

  def distance(a:CosmicLocation, b:CosmicLocation):Length =
    if (a.uuid == b.uuid) {
      distance(a.locU, b.locU) + distance(a.locG, b.locG) + distance(a.locS, b.locS)
    } else {
      Parsecs(Double.NaN)
    }

  trait Syntax {
    implicit class CosmicLocationOps(underlying:CosmicLocation) {
      lazy val universe:Universe = Universe(underlying.uuid)

      lazy val galaxy:Option[Galaxy] =
        for {
          loc <- underlying.locU
          galaxy <- universe.locate(loc)
        } yield galaxy

      lazy val star:Option[Star] =
        for {
          loc <- underlying.locG
          g <- galaxy
          star <- g.locate(loc)
        } yield star

      lazy val planet:Option[Planet] =
        for {
          loc <- underlying.locS
          s <- star
          planet <- s.locate(loc)
        } yield planet

      lazy val inIntergalacticSpace:Boolean = galaxy.isEmpty
      lazy val inInterstellarSpace:Boolean = star.isEmpty
      lazy val inInterplanetarySpace:Boolean = planet.isEmpty
    }
  }
  object syntax extends Syntax

  trait Instances {
    implicit val cosmicLocationHasEq:Eq[CosmicLocation] = Eq.fromUniversalEquals[CosmicLocation]
    implicit val cosmicLocationHasDist:Dist[CosmicLocation] =
      for {
        planet <- Dist[Planet]
      } yield CosmicLocation(
        planet.star.galaxy.universe.uuid,
        Some(planet.star.galaxy.loc),
        Some(planet.star.loc),
        Some(planet.loc)
      )
  }
  object instances extends Instances
}
