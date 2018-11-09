package pseudoglot
package data

import org.scalacheck.Arbitrary
import spire.random.{Dist, Random, Seed}

class ProsodySpec extends xenocosm.test.XenocosmFunSuite {
  import Magic.default
  import Phones.syntax._

  implicit val arbProsody:Arbitrary[Prosody] = Arbitrary(gen.prosody)
  implicit val arbSeed:Arbitrary[Seed] = Arbitrary(interop.gen.seed)

  test("syllable.has.vowels") {
    implicit val dist:Dist[Phones] = Prosody.dist.flatMap(Prosody.syllable)
    forAll { (prosody:Prosody, seed:Seed) =>
      val syllable = Prosody.syllable(prosody).apply(Random.generatorFromSeed(seed))
      syllable.vowels should not be empty
    }
  }
}
