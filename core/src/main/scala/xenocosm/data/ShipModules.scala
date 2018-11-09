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
      Engine(SpeedOfLight * 0.1, CubicMetersPerSecond(0.001)),
      Navigation(Galaxy.scale),
      ShipModule.emptyCargo(CubicMeters(100)),
      FuelTank(CubicMeters(0), CubicMeters(100)),
      EmptyModule
    )

  val navModules:ShipModules => List[Navigation] =
    _ collect { case m:Navigation => m }

  val engineModules:ShipModules => List[Engine] =
    _ collect { case m:Engine => m }

  val fuelModules:ShipModules => List[FuelTank] =
    _ collect { case m:FuelTank => m }

  val maxNavDistance:ShipModules => Length = modules =>
    Try(navModules(modules).map(_.range).max)
      .getOrElse(LightYears(0))

  val unusedFuel:ShipModules => Volume = modules =>
    fuelModules(modules)
      .foldLeft(CubicMeters(0))(_ + _.unused)

  val bestEngine:ShipModules => Option[Engine] = modules =>
    Try(engineModules(modules).maxBy(ShipModule.fuelEfficiency))
      .toOption

  val maxTravelDistance:ShipModules => Length = modules =>
    bestEngine(modules)
      .map(engine => (unusedFuel(modules) / engine.consumptionRate) * engine.speed)
      .getOrElse(LightYears(0))

  // Only consume fuel if we have enough to satisfy the need
  val consumeFuel:ShipModules => Volume => Option[ShipModules] = modules => fuel =>
    modules
      .foldLeft((ShipModules.empty, fuel))({
        case ((acc:ShipModules, need:Volume), tank:FuelTank) =>
          val (updated, remainingNeed) = ShipModule.consumeFuel(tank)(need)
          (updated :: acc, remainingNeed)
        case ((acc:ShipModules, need:Volume), x) => (x :: acc, need)
      }) match {
        case (xs:ShipModules, CubicMeters(0)) => Some(xs)
        case _ => None
      }
}
