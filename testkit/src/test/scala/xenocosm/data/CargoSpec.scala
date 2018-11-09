package xenocosm
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class CargoSpec extends xenocosm.test.XenocosmFunSuite {
  import Cargo.instances._

  implicit val arb:Arbitrary[Cargo] = Arbitrary(gen.cargo)

  checkAll("Eq[Cargo]", EqTests[Cargo].eqv)
}
