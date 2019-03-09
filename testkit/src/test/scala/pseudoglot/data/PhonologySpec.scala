package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.random.Generator
import spire.random.rng.BurtleRot2

class PhonologySpec extends xenocosm.test.XenocosmFunSuite {
  import Phonology.instances._

  implicit val arb:Arbitrary[Phonology] = Arbitrary(gen.phonology)

  checkAll("Eq[Phonology]", EqTests[Phonology].eqv)

  test("dist.proceeds.from.seed") {
    val seed:Array[Int] = genInts.sample.get
    val lhs = Phonology.dist(BurtleRot2.fromSeed(seed))
    val rhs = Phonology.dist(BurtleRot2.fromSeed(seed))

    lhs shouldBe rhs
  }

  test("syllable.has.phones") {
    val rng:Generator = spireRNG
    val phonology:Phonology = Phonology.dist(rng)

    phonology.syllable(rng).toList should not be empty
  }
}
