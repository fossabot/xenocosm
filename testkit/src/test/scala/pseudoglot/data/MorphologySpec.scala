package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.random.rng.BurtleRot2

class MorphologySpec extends xenocosm.test.XenocosmFunSuite {
  import Morphology.instances._

  implicit val arbMorphology:Arbitrary[Morphology] = Arbitrary(gen.morphology)

  checkAll("Eq[Morphology]", EqTests[Morphology].eqv)

  test("dist.proceeds.from.seed") {
    val seed:Array[Int] = genInts.sample.get
    val lhs = Morphology.dist(BurtleRot2.fromSeed(seed))
    val rhs = Morphology.dist(BurtleRot2.fromSeed(seed))

    lhs shouldBe rhs
  }
}
