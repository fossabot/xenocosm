package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class HeightSpec extends XenocosmSuite {
  import Height.instances._
  import spire.std.int._

  checkAll("Order[Height]", OrderLaws[Height].order)
  checkAll("MetricSpace[Height]", VectorSpaceLaws[Height, Int].metricSpace)
}
