package pseudoglot
package data

import cats.instances.option._
import cats.kernel.laws.discipline.OrderTests
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class PlaceSpec extends xenocosm.test.XenocosmFunSuite {
  import Place.instances._

  implicit val arb:Arbitrary[Place] = Arbitrary(pseudoglot.gen.place)

  checkAll("Order[Place]", OrderTests[Place].order)
  checkAll("MetricSpace[Place]", VectorSpaceLaws[Place, Int].metricSpace)

  test("parse failure") {
    Place.parse("") shouldBe Left("")
  }
}
