package xenocosm
package data

import cats.kernel.laws.discipline.EqTests

class TraderSpec extends xenocosm.test.XenocosmFunSuite {
  import Trader.instances._

  checkAll("Eq[Trader]", EqTests[Trader].eqv)
}
