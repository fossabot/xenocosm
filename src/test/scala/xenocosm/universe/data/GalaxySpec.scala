package xenocosm
package universe
package data

import cats.kernel.laws.OrderLaws

class GalaxySpec extends XenocosmSuite {
  import Galaxy.instances._

  checkAll("Eq[Galaxy]", OrderLaws[Galaxy].eqv)
}
