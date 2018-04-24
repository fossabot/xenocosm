package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import galaxique.data._
import spire.random.Dist

final case class CosmicLocation(uuid:UUID, locU:Option[Point3], locG:Option[Point3], locS:Option[Point3])

object CosmicLocation {
  import galaxique.SparseSpace3.syntax._
  import Galaxy.instances._
  import Planet.instances._
  import Star.instances._
  import Universe.instances._

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
