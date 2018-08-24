package xenocosm
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class TraderSpec extends xenocosm.test.XenocosmFunSuite {
  import Trader.instances._

  implicit val arb:Arbitrary[Trader] = Arbitrary(gen.trader)

  checkAll("Eq[Trader]", EqTests[Trader].eqv)
}
