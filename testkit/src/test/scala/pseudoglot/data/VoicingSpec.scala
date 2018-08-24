package pseudoglot
package data

import cats.instances.option._
import cats.kernel.laws.discipline.OrderTests
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class VoicingSpec extends xenocosm.test.XenocosmFunSuite {
  import Voicing.instances._

  implicit val arb:Arbitrary[Voicing] = Arbitrary(pseudoglot.gen.voicing)

  checkAll("Order[Voicing]", OrderTests[Voicing].order)
  checkAll("MetricSpace[Voicing]", VectorSpaceLaws[Voicing, Int].metricSpace)

  test("parse failure") {
    Voicing.parse("") shouldBe Left("")
  }
}
