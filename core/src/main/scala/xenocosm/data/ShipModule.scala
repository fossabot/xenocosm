package xenocosm.data

import cats.kernel.Eq
import spire.random.Dist
import squants.motion.{Velocity, VolumeFlow}
import squants.space._
import squants.time.Time

sealed trait ShipModule
case object EmptyModule extends ShipModule
final case class CargoHold(cargo:Map[Cargo, Volume]) extends ShipModule
final case class FuelTank(used:Volume, unused:Volume) extends ShipModule
final case class Navigation(range:Length) extends ShipModule
final case class Engine(speed:Velocity, consumptionRate:VolumeFlow) extends ShipModule

object ShipModule {
  def empty:ShipModule = EmptyModule
  def emptyCargo(max:Volume):ShipModule = CargoHold(Map(Vacuum -> max))
  def emptyFuel(max:Volume):ShipModule = FuelTank(CubicMeters(0), max)
  def fuelEfficiency(engine:Engine):Double =
    engine.speed.toKilometersPerSecond /
    engine.consumptionRate.toCubicMetersPerSecond

  def consumeFuel(tank:FuelTank, need:Volume):(FuelTank, Volume) =
    if (tank.unused < need) {
      (FuelTank(tank.used + tank.unused, CubicMeters(0)), need - tank.unused)
    } else {
      (FuelTank(tank.used + need, tank.unused - need), CubicMeters(0))
    }

  def travel(engine:Engine, consumedFuel:Volume):(Velocity, Time) =
    (engine.speed, consumedFuel / engine.consumptionRate)

  def fuelNeeded(engine:Engine, from:CosmicLocation, to:CosmicLocation):Volume =
    engine.consumptionRate * ((from distance to) / engine.speed)

  trait Instances {
    implicit val shipModuleHasEq:Eq[ShipModule] = Eq.fromUniversalEquals[ShipModule]
    implicit val shipModuleHasDist:Dist[ShipModule] =
      for {
        volume <- Dist.intrange(0, 100).map(CubicMeters.apply[Int])
        range <- Dist.intrange(1, 3).map(Parsecs.apply[Int])
        cargo = emptyCargo(volume)
        fuel = emptyFuel(volume)
        nav = Navigation(range)
        module <- Dist.oneOf[ShipModule](EmptyModule, cargo, fuel, nav)
      } yield module
  }
  object instances extends Instances
}
