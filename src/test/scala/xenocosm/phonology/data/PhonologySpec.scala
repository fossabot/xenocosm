package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws

class PhonologySpec extends XenocosmSuite {
  import Phonology.instances._

  checkAll("Eq[Phonology]", OrderLaws[Phonology].eqv)
}
