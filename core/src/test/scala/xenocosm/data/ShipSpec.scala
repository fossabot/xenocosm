package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.Point3
import squants.motion.{CubicMetersPerSecond, SpeedOfLight}
import squants.space.{AstronomicalUnits, CubicMeters}
import squants.time.Seconds

class ShipSpec extends xenocosm.test.XenocosmSuite {
  import CosmicLocation.instances._
  import Ship.instances._

  private val universe = UUID.randomUUID()
  private val from = CosmicLocation(universe, Some(Point3.zero), Some(Point3.zero), Some(Point3.zero))

  checkAll("Eq[Ship]", EqTests[Ship].eqv)

  test("Ship.travel") {
    val to = from.copy(locS = Some(Point3(AstronomicalUnits(1), AstronomicalUnits(1), AstronomicalUnits(1))))
    val ship = Ship(UUID.randomUUID(), from, ShipModules.startingLoad)
    val (ship2, time) = Ship.travel(ship, to)

    ship2.loc shouldBe to
    ship2.unusedFuel.floor shouldBe CubicMeters(13)
    time.floor shouldBe Seconds(86430)
  }

  test("Ship.travel.must-have-an-engine") {
    val to = arbFromDist[CosmicLocation].arbitrary.sample.get
    val ship = Ship(UUID.randomUUID(), from, ShipModules.empty)
    val (ship2, time) = Ship.travel(ship, to)

    ship shouldBe ship2
    time shouldBe Seconds(0)
  }

  test("Ship.travel.must-have-fuel") {
    val to = arbFromDist[CosmicLocation].arbitrary.sample.get
    val ship = Ship(UUID.randomUUID(), from, List(Engine(SpeedOfLight, CubicMetersPerSecond(1))))
    val (ship2, time) = ship.travel(to)

    ship shouldBe ship2
    time shouldBe Seconds(0)
  }
}
