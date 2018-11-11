package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.random.Random

class PhonologySpec extends xenocosm.test.XenocosmFunSuite {
  import Phonology.instances._

  implicit val arb:Arbitrary[Phonology] = Arbitrary(gen.phonology)

  checkAll("Eq[Phonology]", EqTests[Phonology].eqv)

  test("syllable.has.phones") {
    val rng = Random.initGenerator()
    forAll { phonology: Phonology =>
      phonology.syllable(rng).toList should not be empty
    }
  }
}
