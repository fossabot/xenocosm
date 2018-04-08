package xenocosm
package data

import cats.kernel.Eq
import galaxique.data._
import spire.random.Dist

sealed trait ShipLocation
case object Lost extends ShipLocation
final case class IntergalacticSpace(universe:Universe, coords:Point3) extends ShipLocation
final case class InterstellarSpace(galaxy:Galaxy, coords:Point3) extends ShipLocation
final case class InterplanetarySpace(star:Star, coords:Point3) extends ShipLocation
final case class Docked(planet:Planet) extends ShipLocation

object ShipLocation {
  import Galaxy.instances._
  import Planet.instances._
  import Star.instances._
  import Universe.instances._

  def lost:ShipLocation = Lost
  def apply(universe:Universe, coords:Point3):ShipLocation = IntergalacticSpace(universe, coords)
  def apply(galaxy:Galaxy, coords:Point3):ShipLocation = InterstellarSpace(galaxy:Galaxy, coords:Point3)
  def apply(star:Star, coords:Point3):ShipLocation = InterplanetarySpace(star:Star, coords:Point3)
  def apply(planet:Planet):ShipLocation = Docked(planet)

  trait Instances {
    implicit val shipLocationHasEq:Eq[ShipLocation] = Eq.fromUniversalEquals[ShipLocation]
    implicit val shipLocationHasDist:Dist[ShipLocation] =
      for {
        intergalactic <- Dist[Universe].map(IntergalacticSpace(_, Point3.zero))
        interstellar <- Dist[Galaxy].map(InterstellarSpace(_, Point3.zero))
        interplanetary <- Dist[Star].map(InterplanetarySpace(_, Point3.zero))
        docked <- Dist[Planet].map(Docked.apply)
        location <- Dist.oneOf(Lost, intergalactic, interstellar, interplanetary, docked)
      } yield location
  }
  object instances extends Instances
}
