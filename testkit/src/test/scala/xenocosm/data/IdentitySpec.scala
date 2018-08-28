package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.{Galaxy, Point3, Star, Universe}
import org.scalacheck.Arbitrary
import spire.math.UInt
import squants.motion.CubicMetersPerSecond
import squants.space.{CubicMeters, LightYears}
import squants.time.Seconds

class IdentitySpec extends xenocosm.test.XenocosmFunSuite {
  import cavernetics.FSM.syntax._
  import Identity.instances._

  private implicit val arb:Arbitrary[Identity] = Arbitrary(gen.identity)

  checkAll("Eq[Identity]", EqTests[Identity].eqv)

  private val locA = CosmicLocation(
    UUID.randomUUID(),
    Some(Point3(Universe.scale, Universe.scale, Universe.scale)),
    Some(Point3(Galaxy.scale, Galaxy.scale, Galaxy.scale)),
    Some(Point3(Star.scale, Star.scale, Star.scale))
  )

  private val identity = Identity(UUID.randomUUID(), None, UInt(1), Some(
    Trader(UUID.randomUUID(), Ship(UUID.randomUUID(), locA, List(
      Navigation(LightYears(1)),
      FuelTank(CubicMeters(0), CubicMeters(1000)),
      Engine(Star.scale / Seconds(1000), CubicMetersPerSecond(1))
    )), ElapsedTime.zero)
  ))

  test("FSM: TraderCreated: success") {
    val traderB = gen.trader.sample.get

    identity.copy(trader = None).transition(TraderCreated(traderB)).map(_.moves) shouldBe Right(UInt(1))
  }

  test("FSM: TraderSelected: success") {
    val traderB = gen.trader.sample.get

    identity.copy(trader = None).transition(TraderSelected(traderB)).map(_.moves) shouldBe Right(UInt(1))
  }

  test("FSM: ShipMoved: no moves remaining") {
    val locSB = locA.locS.map(coords => coords.copy(x = coords.x * 2))
    val locB = locA.copy(locS = locSB)

    identity.copy(moves = UInt(0)).transition(ShipMoved(locB)) shouldBe Left(NoMovesRemaining)
  }

  test("FSM: ShipMoved: success") {
    val locSB = locA.locS.map(coords => coords.copy(x = coords.x * 2))
    val locB = locA.copy(locS = locSB)

    identity.transition(ShipMoved(locB)).map(_.moves) shouldBe Right(UInt(0))
  }
}
