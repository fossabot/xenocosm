package xenocosm
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class ForeignIDSpec extends xenocosm.test.XenocosmFunSuite {
  import ForeignID.instances._

  private implicit val arb:Arbitrary[ForeignID] = Arbitrary(gen.foreignID)

  checkAll("Eq[ForeignID]", EqTests[ForeignID].eqv)
}
