package xenocosm
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class IdentitySpec extends xenocosm.test.XenocosmFunSuite {
  import Identity.instances._

  private implicit val arb:Arbitrary[Identity] = Arbitrary(gen.identity)

  checkAll("Eq[Identity]", EqTests[Identity].eqv)
}
