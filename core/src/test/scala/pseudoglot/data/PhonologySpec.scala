package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests

class PhonologySpec extends xenocosm.test.XenocosmSuite {
  import Phonology.instances._

  checkAll("Eq[Phonology]", EqTests[Phonology].eqv)
}
