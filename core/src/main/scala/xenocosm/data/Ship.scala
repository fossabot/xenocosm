package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import monocle.Lens
import monocle.macros.GenLens
import spire.random.Dist
import squants.space.{CubicMeters, Length, Volume}
import squants.time.{Seconds, Time}

final case class Ship(uuid:UUID, loc:CosmicLocation, modules:ShipModules) { self =>
  def travel(to:CosmicLocation):(Ship, Time) = Ship.travel(self, to)
  lazy val maxNavDistance:Length = ShipModules.maxNavDistance(modules)
  lazy val maxTravelDistance:Length = ShipModules.maxTravelDistance(modules)
  lazy val unusedFuel:Volume = ShipModules.unusedFuel(modules)
}

object Ship {
  import interop.java.instances._
  import CosmicLocation.instances._

  private val loc:Lens[Ship, CosmicLocation] = GenLens[Ship](_.loc)
  private val modules:Lens[Ship, ShipModules] = GenLens[Ship](_.modules)
  private val update:CosmicLocation => ShipModules => Ship => Ship =
    x => ys => modules.set(ys) andThen loc.set(x)

  def travel(ship:Ship, to:CosmicLocation):(Ship, Time) =
    ShipModules
      .bestEngine(ship.modules)
      .map(engine => (engine, ShipModule.fuelNeeded(engine, ship.loc, to)))
      .map({ case (engine:Engine, needed:Volume) =>
        ShipModules.consumeFuel(ship.modules, needed) match {
          case (xs:ShipModules, CubicMeters(0)) =>
            val (_, time) = ShipModule.travel(engine, needed)
            (update(to)(xs)(ship), time)
          case _ => (ship, Seconds(0))
        }
      })
      .getOrElse((ship, Seconds(0)))

  trait Instances {
    implicit val shipHasEq:Eq[Ship] = Eq.fromUniversalEquals[Ship]
    implicit val shipHasDist:Dist[Ship] =
      for {
        uuid <- Dist[UUID]
        loc <- Dist[CosmicLocation]
      } yield Ship(uuid, loc, ShipModules.startingLoad)
  }
  object instances extends Instances
}
