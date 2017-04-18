package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class VoicingSpec extends XenocosmSuite {
  import Voicing.instances._
  import spire.std.int._

  checkAll("Order[Voicing]", OrderLaws[Voicing].order)
  checkAll("MetricSpace[Voicing]", VectorSpaceLaws[Voicing, Int].metricSpace)
}
