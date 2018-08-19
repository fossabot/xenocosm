package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.Point3
import squants.motion.{CubicMetersPerSecond, SpeedOfLight}
import squants.space.{AstronomicalUnits, CubicMeters}
import squants.time.Seconds

class ShipSpec extends xenocosm.test.XenocosmFunSuite {
  import CosmicLocation.instances._
  import Ship.instances._

  private val universe = UUID.randomUUID()
  private val from = CosmicLocation(universe, Some(Point3.zero), Some(Point3.zero), Some(Point3.zero))

  checkAll("Eq[Ship]", EqTests[Ship].eqv)

  test("Ship.travel") {
    val loc = from.copy(locS = Some(Point3(AstronomicalUnits(1), AstronomicalUnits(1), AstronomicalUnits(1))))
    val ship = Ship(UUID.randomUUID(), from, ShipModules.startingLoad)
    val (ship2, elapsedShip, elapsedObjective) = ship.travelTo(loc)

    ship2.loc shouldBe loc
    ship2.unusedFuel.floor shouldBe CubicMeters(13)
    elapsedShip.floor shouldBe Seconds(86430)
    elapsedObjective shouldBe Seconds(86434.48571474903)
  }

  test("Ship.travel.must-have-an-engine") {
    val loc = arbFromDist[CosmicLocation].arbitrary.sample.get
    val ship = Ship(UUID.randomUUID(), from, ShipModules.empty)
    val (ship2, elapsedShip, elapsedObjective) = ship.travelTo(loc)

    ship shouldBe ship2
    elapsedShip shouldBe Seconds(0)
    elapsedObjective shouldBe Seconds(0)
  }

  test("Ship.travel.must-have-fuel") {
    val loc = arbFromDist[CosmicLocation].arbitrary.sample.get
    val ship = Ship(UUID.randomUUID(), from, List(Engine(SpeedOfLight, CubicMetersPerSecond(1))))
    val (ship2, elapsedShip, elapsedObjective) = ship.travelTo(loc)

    ship shouldBe ship2
    elapsedShip shouldBe Seconds(0)
    elapsedObjective shouldBe Seconds(0)
  }
}
