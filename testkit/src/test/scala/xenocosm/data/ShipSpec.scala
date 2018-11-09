package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.{Galaxy, Point3, Star, Universe}
import org.scalacheck.Arbitrary
import squants.motion.CubicMetersPerSecond
import squants.space.{CubicMeters, LightYears}
import squants.time.Seconds

class ShipSpec extends xenocosm.test.XenocosmFunSuite {
  import cavernetics.FSM.syntax._
  import Ship.instances._

  implicit val arb:Arbitrary[Ship] = Arbitrary(gen.ship)

  checkAll("Eq[Ship]", EqTests[Ship].eqv)

  private val locA = CosmicLocation(
    UUID.randomUUID(),
    Some(Point3(Universe.scale, Universe.scale, Universe.scale)),
    Some(Point3(Galaxy.scale, Galaxy.scale, Galaxy.scale)),
    Some(Point3(Star.scale, Star.scale, Star.scale))
  )

  private val ship = Ship(UUID.randomUUID(), locA, List(
    Navigation(LightYears(1)),
    FuelTank(CubicMeters(0), CubicMeters(1000)),
    Engine(Star.scale / Seconds(1000), CubicMetersPerSecond(1))
  ))

  test("FSM: ShipMoved: too far to navigate") {
    val locSB = locA.locS.map(coords => coords.copy(x = coords.x + LightYears(2)))
    val locB = locA.copy(locS = locSB)

    ship.transition(ShipMoved(locB)) shouldBe Left(CannotNavigate(ship.maxNavDistance))
  }

  test("FSM: ShipMoved: not enough fuel") {
    val locSB = locA.locS.map(coords => coords.copy(x = coords.x * 3))
    val locB = locA.copy(locS = locSB)

    ship.transition(ShipMoved(locB)) shouldBe Left(NotEnoughFuel(ship.unusedFuel))
  }

  test("FSM: ShipMoved: success") {
    val locSB = locA.locS.map(coords => coords.copy(x = coords.x * 2))
    val locB = locA.copy(locS = locSB)

    ship.transition(ShipMoved(locB)).map(_.loc) shouldBe Right(locB)
  }
}
