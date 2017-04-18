package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class BacknessSpec extends XenocosmSuite {
  import Backness.instances._
  import spire.std.int._

  checkAll("Order[Backness]", OrderLaws[Backness].order)
  checkAll("MetricSpace[Backness]", VectorSpaceLaws[Backness, Int].metricSpace)
}
