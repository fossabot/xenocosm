package pseudoglot
package data

import cats.instances.option._
import cats.kernel.laws.discipline.OrderTests
import spire.laws._
import spire.std.int._

class VoicingSpec extends xenocosm.test.XenocosmSuite {
  import Voicing.instances._

  checkAll("Order[Voicing]", OrderTests[Voicing].order)
  checkAll("MetricSpace[Voicing]", VectorSpaceLaws[Voicing, Int].metricSpace)

  test("parse failure") {
    Voicing.parse("") shouldBe Left("")
  }
}
