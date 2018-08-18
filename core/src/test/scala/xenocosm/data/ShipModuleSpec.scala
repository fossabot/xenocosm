package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.Point3
import squants.motion.{CubicMetersPerSecond, MetersPerSecond, SpeedOfLight}
import squants.space.{AstronomicalUnits, CubicMeters}
import squants.time.Seconds

class ShipModuleSpec extends xenocosm.test.XenocosmSuite {
  import ShipModule.instances._

  checkAll("Eq[ShipModule]", EqTests[ShipModule].eqv)

  test("ShipModule.fuelEfficiency") {
    val engine = Engine(SpeedOfLight * 0.01, CubicMetersPerSecond(0.01))
    ShipModule.fuelEfficiency(engine) shouldBe 299792.458
  }

  test("ShipModule.consumeFuel") {
    val tank = FuelTank(CubicMeters(0), CubicMeters(100))

    val (tank1, remaining1) = ShipModule.consumeFuel(tank, CubicMeters(99))
    tank1 shouldBe FuelTank(CubicMeters(99), CubicMeters(1))
    remaining1 shouldBe CubicMeters(0)

    val (tank2, remaining2) = ShipModule.consumeFuel(tank, CubicMeters(100))
    tank2 shouldBe FuelTank(CubicMeters(100), CubicMeters(0))
    remaining2 shouldBe CubicMeters(0)

    val (tank3, remaining3) = ShipModule.consumeFuel(tank, CubicMeters(101))
    tank3 shouldBe FuelTank(CubicMeters(100), CubicMeters(0))
    remaining3 shouldBe CubicMeters(1)
  }

  test("ShipModule.travel") {
    val engine = Engine(SpeedOfLight * 0.01, CubicMetersPerSecond(0.01))
    val consumedFuel = CubicMeters(100)
    val (velocity, time) = ShipModule.travel(engine, consumedFuel)

    velocity shouldBe MetersPerSecond(2997924.58)
    time shouldBe Seconds(10000)
  }

  test("ShipModule.fuelNeeded") {
    val engine = Engine(AstronomicalUnits(1) / Seconds(1), CubicMetersPerSecond(0.01))
    val planet1 = CosmicLocation(UUID.randomUUID(), Some(Point3.zero), Some(Point3.zero), Some(Point3.zero))
    val planet2 = planet1.copy(locS = Some(Point3(AstronomicalUnits(1), AstronomicalUnits(0), AstronomicalUnits(0))))
    val fuelNeeded = ShipModule.fuelNeeded(engine, planet1, planet2)

    fuelNeeded shouldBe CubicMeters(0.01)
  }
}
