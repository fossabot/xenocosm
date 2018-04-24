package xenocosm
package data

import cats.kernel.laws.discipline.EqTests

class CosmicLocationSpec extends xenocosm.test.XenocosmSuite {
  import CosmicLocation.instances._

  checkAll("Eq[CosmicLocation]", EqTests[CosmicLocation].eqv)
}
