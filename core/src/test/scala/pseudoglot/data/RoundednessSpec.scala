package pseudoglot
package data

import cats.instances.option._
import cats.kernel.laws.discipline.OrderTests
import spire.laws._
import spire.std.int._

class RoundednessSpec extends xenocosm.test.XenocosmSuite {
  import Roundedness.instances._

  checkAll("Order[Roundedness]", OrderTests[Roundedness].order)
  checkAll("MetricSpace[Roundedness]", VectorSpaceLaws[Roundedness, Int].metricSpace)

  test("parse failure") {
    Roundedness.parse("") shouldBe Left("")
  }
}
