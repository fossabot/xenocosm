package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class PlaceSpec extends XenocosmSuite {
  import Place.instances._
  import spire.std.int._

  checkAll("Order[Place]", OrderLaws[Place].order)
  checkAll("MetricSpace[Place]", VectorSpaceLaws[Place, Int].metricSpace)
}
