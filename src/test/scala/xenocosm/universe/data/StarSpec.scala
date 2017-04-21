package xenocosm
package universe
package data

import cats.kernel.laws.OrderLaws

class StarSpec extends XenocosmSuite {
  import Star.instances._

  checkAll("Eq[Star]", OrderLaws[Star].eqv)
}
