package xenocosm
package data

import scala.util.Try
import galaxique.data.Galaxy
import squants.motion.{CubicMetersPerSecond, SpeedOfLight}
import squants.space._

object ShipModules {
  val empty:ShipModules = List.empty[ShipModule]

  lazy val startingLoad:ShipModules =
    List(
      Engine(SpeedOfLight * 0.01, CubicMetersPerSecond(0.001)),
      Navigation(Galaxy.scale),
      ShipModule.emptyCargo(CubicMeters(100)),
      FuelTank(CubicMeters(0), CubicMeters(100)),
      EmptyModule
    )

  def navModules(modules:ShipModules):List[Navigation] =
    modules.collect({ case m @ Navigation(_) => m })

  def engineModules(modules:ShipModules):List[Engine] =
    modules.collect({ case m @ Engine(_, _) => m })

  def fuelModules(modules:ShipModules):List[FuelTank] =
    modules.collect({ case m @ FuelTank(_, _) => m })

  def maxNavDistance(modules:ShipModules):Length =
    Try(navModules(modules).map(_.range).max).getOrElse(LightYears(0))

  def unusedFuel(modules:ShipModules):Volume =
    fuelModules(modules).foldLeft(CubicMeters(0))(_ + _.unused)

  def bestEngine(modules:ShipModules):Option[Engine] =
    Try(engineModules(modules).maxBy(ShipModule.fuelEfficiency)).toOption

  def maxTravelDistance(modules:ShipModules):Length =
    bestEngine(modules)
      .map(engine => (unusedFuel(modules) / engine.consumptionRate) * engine.speed)
      .getOrElse(LightYears(0))

  def consumeFuel(modules:ShipModules, fuel:Volume):(ShipModules, Volume) =
    modules.foldLeft((ShipModules.empty, fuel))({
      case ((acc:ShipModules, need:Volume), tank:FuelTank) =>
        val (updated, remainingNeed) = ShipModule.consumeFuel(tank, need)
        (updated :: acc, remainingNeed)
      case ((acc:ShipModules, need:Volume), x) => (x::acc, need)
    })
}
