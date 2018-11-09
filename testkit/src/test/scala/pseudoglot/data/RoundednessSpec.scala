package pseudoglot
package data

import cats.instances.option._
import cats.kernel.laws.discipline.OrderTests
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class RoundednessSpec extends xenocosm.test.XenocosmFunSuite {
  import Roundedness.instances._

  implicit val arb:Arbitrary[Roundedness] = Arbitrary(pseudoglot.gen.roundedness)

  checkAll("Order[Roundedness]", OrderTests[Roundedness].order)
  checkAll("MetricSpace[Roundedness]", VectorSpaceLaws[Roundedness, Int].metricSpace)

  test("parse failure") {
    Roundedness.parse("") shouldBe Left("")
  }
}
