package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class PhonologySpec extends xenocosm.test.XenocosmFunSuite {
  import Phonology.instances._

  implicit val arb:Arbitrary[Phonology] = Arbitrary(gen.phonology)

  checkAll("Eq[Phonology]", EqTests[Phonology].eqv)
}
