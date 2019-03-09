package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.random.rng.BurtleRot2

class OnomasticRuleSpec extends xenocosm.test.XenocosmFunSuite {
  import OnomasticRule.instances._

  implicit val arb:Arbitrary[OnomasticRule] = Arbitrary(gen.onomasticRule)

  checkAll("Eq[OnomasticRule]", EqTests[OnomasticRule].eqv)

  test("dist.proceeds.from.seed") {
    val seed:Array[Int] = genInts.sample.get
    val lhs = OnomasticRule.dist(BurtleRot2.fromSeed(seed))
    val rhs = OnomasticRule.dist(BurtleRot2.fromSeed(seed))

    lhs shouldBe rhs
  }
}
