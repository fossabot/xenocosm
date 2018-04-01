package pseudoglot
package data

import cats.instances.option._
import cats.kernel.laws.discipline.OrderTests
import spire.laws._
import spire.std.int._

class PlaceSpec extends xenocosm.test.XenocosmSuite {
  import Place.instances._

  checkAll("Order[Place]", OrderTests[Place].order)
  checkAll("MetricSpace[Place]", VectorSpaceLaws[Place, Int].metricSpace)

  test("parse failure") {
    Place.parse("") shouldBe Left("")
  }
}
