package xenocosm
package data

import cats.kernel.laws.discipline.EqTests

class ShipSpec extends xenocosm.test.XenocosmSuite {
  import Ship.instances._

  checkAll("Eq[Ship]", EqTests[Ship].eqv)
}
