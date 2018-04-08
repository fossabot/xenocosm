package xenocosm
package data

import cats.kernel.laws.discipline.EqTests

class ShipLocationSpec extends xenocosm.test.XenocosmSuite {
  import ShipLocation.instances._

  checkAll("Eq[ShipLocation]", EqTests[ShipLocation].eqv)
}
