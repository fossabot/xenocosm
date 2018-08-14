package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import spire.random.Dist

class PhonologySpec extends xenocosm.test.XenocosmSuite {
  import Magic.default
  import Phonology.instances._

  implicit val dist:Dist[Phonology] = Phonology.dist

  checkAll("Eq[Phonology]", EqTests[Phonology].eqv)
}
