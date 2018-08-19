package pseudoglot
package data

import spire.random.Dist

class ProsodySpec extends xenocosm.test.XenocosmFunSuite {
  import Magic.default
  import Phones.syntax._

  test("syllable.has.vowels") {
    implicit val dist:Dist[Phones] = Prosody.dist.flatMap(Prosody.syllable)
    forAll { syllable:Phones =>
      syllable.vowels should not be empty
    }
  }
}
