package xenocosm
package phonology

import data.{Pulmonic, Vowel}

class IPASpec extends XenocosmSuite {
  import Pulmonic.instances._
  import Vowel.instances._

  test("IPA.Pulmonic.isomorphism") {
    forAll { (a:Pulmonic) ⇒ IPA.pulmonics.values.filter(IPA.pulmonics.get(a).contains).toVector.size == 1 }
  }

  test("IPA.Vowel.isomorphism") {
    forAll { (a:Vowel) ⇒ IPA.vowels.values.filter(IPA.vowels.get(a).contains).toVector.size == 1 }
  }

}
