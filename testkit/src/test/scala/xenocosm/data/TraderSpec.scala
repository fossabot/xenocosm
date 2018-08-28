package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.{Galaxy, Point3, Star, Universe}
import org.scalacheck.Arbitrary
import squants.motion.CubicMetersPerSecond
import squants.space.{CubicMeters, LightYears}
import squants.time.Seconds

class TraderSpec extends xenocosm.test.XenocosmFunSuite {
  import cavernetics.FSM.syntax._
  import Trader.instances._

  implicit val arb:Arbitrary[Trader] = Arbitrary(gen.trader)

  checkAll("Eq[Trader]", EqTests[Trader].eqv)

  private val locA = CosmicLocation(
    UUID.randomUUID(),
    Some(Point3(Universe.scale, Universe.scale, Universe.scale)),
    Some(Point3(Galaxy.scale, Galaxy.scale, Galaxy.scale)),
    Some(Point3(Star.scale, Star.scale, Star.scale))
  )

  private val trader = Trader(UUID.randomUUID(), Ship(UUID.randomUUID(), locA, List(
    Navigation(LightYears(1)),
    FuelTank(CubicMeters(0), CubicMeters(1000)),
    Engine(Star.scale / Seconds(1000), CubicMetersPerSecond(1))
  )), ElapsedTime.zero)

  test("FSM: ShipMoved: success") {
    val locSB = locA.locS.map(coords => coords.copy(x = coords.x * 2))
    val locB = locA.copy(locS = locSB)
    val result = trader.transition(ShipMoved(locB))

    result.map(_.elapsed) shouldBe Right(ElapsedTime(Seconds(999.9999999999999), Seconds(1153.9359431681949)))
  }
}
