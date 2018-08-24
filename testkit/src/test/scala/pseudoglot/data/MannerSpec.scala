package pseudoglot
package data

import cats.kernel.laws.discipline.OrderTests
import cats.instances.option._
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class MannerSpec extends xenocosm.test.XenocosmFunSuite {
  import Manner.instances._

  implicit val arb:Arbitrary[Manner] = Arbitrary(pseudoglot.gen.manner)

  checkAll("Order[Manner]", OrderTests[Manner].order)
  checkAll("MetricSpace[Manner]", VectorSpaceLaws[Manner, Int].metricSpace)

  test("parse failure") {
    Manner.parse("") shouldBe Left("")
  }
}
