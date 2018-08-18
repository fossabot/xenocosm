package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import monocle.Lens
import monocle.macros.GenLens
import spire.random.Dist
import squants.motion.{SpeedOfLight, Velocity}
import squants.space.{CubicMeters, Length, Volume}
import squants.time.{Seconds, Time}

final case class Ship(uuid:UUID, loc:CosmicLocation, modules:ShipModules) { self =>
  def travelTo(to:CosmicLocation):(Ship, Time, Time) =
    Ship.maybeTravel(self, to).getOrElse((self, Seconds(0), Seconds(0)))
  lazy val maxNavDistance:Length = ShipModules.maxNavDistance(modules)
  lazy val maxTravelDistance:Length = ShipModules.maxTravelDistance(modules)
  lazy val unusedFuel:Volume = ShipModules.unusedFuel(modules)
}

object Ship {
  import interop.java.instances._
  import CosmicLocation.instances._

  private val loc:Lens[Ship, CosmicLocation] = GenLens[Ship](_.loc)
  private val modules:Lens[Ship, ShipModules] = GenLens[Ship](_.modules)

  private val v2c2:Velocity => Double =
    velocity =>
      (velocity.toMetersPerSecond * velocity.toMetersPerSecond) /
      (SpeedOfLight.toMetersPerSecond * SpeedOfLight.toMetersPerSecond)

  val objectiveElapsedTime:Velocity => Time => Time =
    velocity => shipTime => shipTime / Math.sqrt(1 - v2c2(velocity))

  // Only consume fuel if we have enough to satisfy the need
  val consumeFuel:Ship => Volume => Option[Ship] =
    ship => needed => ShipModules.consumeFuel(ship.modules, needed) match {
      case (xs:ShipModules, CubicMeters(0)) => Some(modules.set(xs)(ship))
      case _ => None
    }

  def maybeTravel(ship:Ship, to:CosmicLocation):Option[(Ship, Time, Time)] =
    for {
      engine <- ShipModules.bestEngine(ship.modules)
      demand = ShipModule.fuelNeeded(engine, ship.loc, to)
      consumed <- consumeFuel(ship)(demand)
      (velocity, time) = ShipModule.travel(engine, demand)
    } yield (loc.set(to)(consumed), time, objectiveElapsedTime(velocity)(time))

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
