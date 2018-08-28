package xenocosm.data

import cats.kernel.Eq
import spire.random.Dist
import squants.motion.{Velocity, VolumeFlow}
import squants.space._

sealed trait ShipModule
case object EmptyModule extends ShipModule
final case class CargoHold(cargo:Map[Cargo, Volume]) extends ShipModule
final case class FuelTank(used:Volume, unused:Volume) extends ShipModule
final case class Navigation(range:Length) extends ShipModule
final case class Engine(speed:Velocity, consumptionRate:VolumeFlow) extends ShipModule

object ShipModule {
  val empty:ShipModule = EmptyModule

  val emptyCargo:Volume => ShipModule = max => CargoHold(Map(Vacuum -> max))
  val emptyFuel:Volume => ShipModule = FuelTank(CubicMeters(0), _)

  val fuelEfficiency:Engine => Double = engine =>
    engine.speed.toKilometersPerSecond /
    engine.consumptionRate.toCubicMetersPerSecond

  val consumeFuel:FuelTank => Volume => (FuelTank, Volume) = tank => need =>
    if (tank.unused < need) {
      (FuelTank(tank.used + tank.unused, CubicMeters(0)), need - tank.unused)
    } else {
      (FuelTank(tank.used + need, tank.unused - need), CubicMeters(0))
    }

  val travelTime:Engine => Volume => ElapsedTime = engine => demand =>
    ElapsedTime.fromVelocity(engine.speed, demand / engine.consumptionRate)

  def fuelNeeded(engine:Engine, distance:Length):Volume =
    engine.consumptionRate * (distance / engine.speed)

  def fuelNeeded(engine:Engine, from:CosmicLocation, to:CosmicLocation):Volume =
    fuelNeeded(engine, from distance to)

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
