package xenocosm.data

import spire.random.Dist
import squants.space.{CubicMeters, Length, Parsecs, Volume}

sealed trait ShipModule
case object EmptyModule extends ShipModule
final case class CargoHold(used:Volume, unused:Volume) extends ShipModule
final case class FuelTank(used:Volume, unused:Volume) extends ShipModule
final case class Navigation(range:Length) extends ShipModule

object ShipModule {
  def empty:ShipModule = EmptyModule
  def emptyCargo(max:Volume):ShipModule = CargoHold(CubicMeters(0), max)
  def emptyFuel(max:Volume):ShipModule = FuelTank(CubicMeters(0), max)

  trait Instances {
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
