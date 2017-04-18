package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class RoundednessSpec extends XenocosmSuite {
  import Roundedness.instances._
  import spire.std.int._

  checkAll("Order[Roundedness]", OrderLaws[Roundedness].order)
  checkAll("MetricSpace[Roundedness]", VectorSpaceLaws[Roundedness, Int].metricSpace)
}
