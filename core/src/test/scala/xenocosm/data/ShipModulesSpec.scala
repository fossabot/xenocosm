package xenocosm
package data

import squants.motion.{CubicMetersPerSecond, KilometersPerSecond, SpeedOfLight}
import squants.space.{CubicMeters, Meters, Parsecs}

class ShipModulesSpec extends xenocosm.test.XenocosmSuite {

  test("ShipModules.consumeFuel") {
    val (modules1, remaining1) = ShipModules.consumeFuel(List(
      FuelTank(CubicMeters(0), CubicMeters(100)),
      EmptyModule
    ), CubicMeters(90))
    val (modules2, remaining2) = ShipModules.consumeFuel(List(
      FuelTank(CubicMeters(5), CubicMeters(20)),
      FuelTank(CubicMeters(5), CubicMeters(20)),
      FuelTank(CubicMeters(5), CubicMeters(20)),
      FuelTank(CubicMeters(5), CubicMeters(20))
    ), CubicMeters(70))

    modules1.size shouldBe 2
    ShipModules.unusedFuel(modules1) shouldBe CubicMeters(10)
    remaining1 shouldBe CubicMeters(0)

    modules2.size shouldBe 4
    ShipModules.unusedFuel(modules2) shouldBe CubicMeters(10)
    remaining2 shouldBe CubicMeters(0)
  }

  test("ShipModules.bestEngine") {
    val engines0 = ShipModules.empty
    val engines1 = List(Engine(KilometersPerSecond(1), CubicMetersPerSecond(1000)))
    val engines2 = List(
      Engine(KilometersPerSecond(1), CubicMetersPerSecond(1000)),
      Engine(KilometersPerSecond(1), CubicMetersPerSecond(100))
    )
    val engines3 = List(
      Engine(KilometersPerSecond(1), CubicMetersPerSecond(100)),
      Engine(KilometersPerSecond(1), CubicMetersPerSecond(100))
    )

    ShipModules.bestEngine(engines0) shouldBe None
    ShipModules.bestEngine(engines1) shouldBe engines1.headOption
    ShipModules.bestEngine(engines2) shouldBe engines2.lastOption
    ShipModules.bestEngine(engines3) shouldBe engines3.headOption
  }

  test("ShipModules.unusedFuel") {
    val none = ShipModules.empty
    val tank = List(FuelTank(CubicMeters(0), CubicMeters(100)))
    val tank2 = List(
      FuelTank(CubicMeters(0), CubicMeters(100)),
      FuelTank(CubicMeters(0), CubicMeters(100))
    )

    ShipModules.unusedFuel(none) shouldBe CubicMeters(0)
    ShipModules.unusedFuel(tank) shouldBe CubicMeters(100)
    ShipModules.unusedFuel(tank2) shouldBe CubicMeters(200)
  }

  test("ShipModules.maxNavDistance") {
    val none = ShipModules.empty
    val nav = List(Navigation(Parsecs(1)))
    val nav2 = List(
      Navigation(Parsecs(1)),
      Navigation(Parsecs(1.5))
    )

    ShipModules.maxNavDistance(none) shouldBe Meters(0)
    ShipModules.maxNavDistance(nav) shouldBe Parsecs(1)
    ShipModules.maxNavDistance(nav2) shouldBe Parsecs(1.5)
  }

  test("ShipModules.maxTravelDistance") {
    val none = ShipModules.empty
    val noFuel = List(Engine(SpeedOfLight, CubicMetersPerSecond(1)))
    val noEngine = List(FuelTank(CubicMeters(0), CubicMeters(1)))
    val modules = List(
      Engine(SpeedOfLight, CubicMetersPerSecond(1)),
      FuelTank(CubicMeters(0), CubicMeters(1))
    )

    ShipModules.maxTravelDistance(none) shouldBe Meters(0)
    ShipModules.maxTravelDistance(noFuel) shouldBe Meters(0)
    ShipModules.maxTravelDistance(noEngine) shouldBe Meters(0)
    ShipModules.maxTravelDistance(modules) shouldBe Meters(299792458)
  }
}
