package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import cavernetics.FSM
import monocle.Lens
import monocle.macros.GenLens
import spire.random.Dist
import squants.space.{Length, Volume}

final case class Ship(uuid:UUID, loc:CosmicLocation, modules:ShipModules) { self =>
  lazy val maxNavDistance:Length = ShipModules.maxNavDistance(modules)
  lazy val maxTravelDistance:Length = ShipModules.maxTravelDistance(modules)
  lazy val unusedFuel:Volume = ShipModules.unusedFuel(modules)
  lazy val bestEngine:Option[Engine] = ShipModules.bestEngine(modules)
}

object Ship {
  import interop.java.instances._
  import CosmicLocation.instances._

  val locPL:Lens[Ship, CosmicLocation] = GenLens[Ship](_.loc)
  val modulesPL:Lens[Ship, ShipModules] = GenLens[Ship](_.modules)

  // Only return a volume of fuel if there is an engine to consume it
  val fuelNeeded:Ship => CosmicLocation => Option[Volume] = ship => loc =>
    ship.bestEngine.map(ShipModule.fuelNeeded(_, ship.loc, loc))

  val consumeFuel:Ship => CosmicLocation => Option[ShipModules] = ship => loc =>
    for {
      demand <- fuelNeeded(ship)(loc)
      consumed <- ShipModules.consumeFuel(ship.modules)(demand)
    } yield consumed

  val canNavigate:Ship => CosmicLocation => Boolean = ship => loc =>
    ship.maxNavDistance >= (ship.loc distance loc)

  val enoughFuel:Ship => CosmicLocation => Boolean = ship => loc =>
    ship.maxTravelDistance >= (ship.loc distance loc)

  trait Instances {
    implicit val shipHasEq:Eq[Ship] = Eq.fromUniversalEquals[Ship]

    implicit val shipHasDist:Dist[Ship] =
      for {
        uuid <- Dist[UUID]
        loc <- Dist[CosmicLocation]
      } yield Ship(uuid, loc, ShipModules.startingLoad)

    implicit val shipHasFSM:FSM[Ship, XenocosmEvent, XenocosmError] = FSM {
      case (ship, ShipMoved(loc)) if !canNavigate(ship)(loc) =>
        Left(CannotNavigate(ship.maxNavDistance))

      case (ship, ShipMoved(loc)) =>
        consumeFuel(ship)(loc)
          .map(modules => (modulesPL.set(modules) andThen locPL.set(loc))(ship))
          .toRight(NotEnoughFuel(ship.unusedFuel))
    }
  }
  object instances extends Instances
}
