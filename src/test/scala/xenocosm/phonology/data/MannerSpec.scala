package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class MannerSpec extends XenocosmSuite {
  import Manner.instances._
  import spire.std.int._

  checkAll("Order[Manner]", OrderLaws[Manner].order)
  checkAll("MetricSpace[Manner]", VectorSpaceLaws[Manner, Int].metricSpace)
}
