package xenocosm.data

import squants.space.{Length, Volume}

sealed trait ShipModule
case object EmptyModule extends ShipModule
final case class CargoHold(used:Volume, unused:Volume) extends ShipModule
final case class FuelTank(used:Volume, unused:Volume) extends ShipModule
final case class Navigation(range:Length) extends ShipModule
