package xenocosm
package data

import galaxique.data._
import spire.random.Dist

sealed trait ShipLocation
case object Lost extends ShipLocation
final case class IntergalacticSpace(universe:Universe, coords:Point3) extends ShipLocation
final case class InterstellarSpace(galaxy:Galaxy, coords:Point3) extends ShipLocation
final case class InterplanetarySpace(star:Star, coords:Point3) extends ShipLocation
final case class Docked(planet:Planet) extends ShipLocation

object ShipLocation {
  import Planet.instances._

  def lost:ShipLocation = Lost
  def apply(universe:Universe, coords:Point3):ShipLocation = IntergalacticSpace(universe, coords)
  def apply(galaxy:Galaxy, coords:Point3):ShipLocation = InterstellarSpace(galaxy:Galaxy, coords:Point3)
  def apply(star:Star, coords:Point3):ShipLocation = InterplanetarySpace(star:Star, coords:Point3)
  def apply(planet:Planet):ShipLocation = Docked(planet)

  trait Instances {
    implicit val shipLocationHasDist:Dist[ShipLocation] = Dist[Planet].map(Docked.apply)
  }
  object instances extends Instances
}
