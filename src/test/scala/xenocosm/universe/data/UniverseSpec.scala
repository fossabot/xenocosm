package xenocosm
package universe
package data

import cats.kernel.laws.OrderLaws

class UniverseSpec extends XenocosmSuite {
  import Universe.instances._

  checkAll("Eq[Universe]", OrderLaws[Universe].eqv)
}
