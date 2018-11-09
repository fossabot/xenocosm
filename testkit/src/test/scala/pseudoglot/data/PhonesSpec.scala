package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class PhonesSpec extends xenocosm.test.XenocosmFunSuite {
  import Phones.instances._

  implicit val arb:Arbitrary[Phones] = Arbitrary(gen.phones)

  checkAll("Eq[Phones]", EqTests[Phones].eqv)
}
