package pseudoglot
package data

import cats.kernel.laws.discipline.OrderTests
import cats.instances.option._
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class HeightSpec extends xenocosm.test.XenocosmFunSuite {
  import Height.instances._

  implicit val arb:Arbitrary[Height] = Arbitrary(pseudoglot.gen.height)

  checkAll("Order[Height]", OrderTests[Height].order)
  checkAll("MetricSpace[Height]", VectorSpaceLaws[Height, Int].metricSpace)

  test("parse failure") {
    Height.parse("") shouldBe Left("")
  }
}
