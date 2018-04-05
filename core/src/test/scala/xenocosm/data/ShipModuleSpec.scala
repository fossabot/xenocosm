package xenocosm
package data

import cats.kernel.laws.discipline.EqTests

class ShipModuleSpec extends xenocosm.test.XenocosmSuite {
  import ShipModule.instances._

  checkAll("Eq[ShipModule]", EqTests[ShipModule].eqv)
}
