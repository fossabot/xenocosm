package pseudoglot
package data

import cats.kernel.laws.discipline.OrderTests
import cats.instances.option._
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class BacknessSpec extends xenocosm.test.XenocosmFunSuite {
  import Backness.instances._

  implicit val arb:Arbitrary[Backness] = Arbitrary(pseudoglot.gen.backness)

  checkAll("Order[Backness]", OrderTests[Backness].order)
  checkAll("MetricSpace[Backness]", VectorSpaceLaws[Backness, Int].metricSpace)

  test("parse failure") {
    Backness.parse("") shouldBe Left("")
  }
}
