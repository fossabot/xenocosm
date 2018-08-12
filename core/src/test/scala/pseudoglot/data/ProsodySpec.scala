package pseudoglot
package data

import spire.random.Dist

class ProsodySpec extends xenocosm.test.XenocosmSuite {
  import Phones.syntax._
  import Prosody.instances._

  test("syllable.has.vowels") {
    implicit val dist:Dist[Phones] = Dist[Prosody].flatMap(Prosody.syllable)
    forAll { syllable:Phones =>
      syllable.vowels should not be empty
    }
  }
}
