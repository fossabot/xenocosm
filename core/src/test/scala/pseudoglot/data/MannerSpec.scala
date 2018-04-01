package pseudoglot
package data

import cats.kernel.laws.discipline.OrderTests
import cats.instances.option._
import spire.laws._
import spire.std.int._

class MannerSpec extends xenocosm.test.XenocosmSuite {
  import Manner.instances._

  checkAll("Order[Manner]", OrderTests[Manner].order)
  checkAll("MetricSpace[Manner]", VectorSpaceLaws[Manner, Int].metricSpace)

  test("parse failure") {
    Manner.parse("") shouldBe Left("")
  }
}
