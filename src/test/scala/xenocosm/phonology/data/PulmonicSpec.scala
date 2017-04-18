package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class PulmonicSpec extends XenocosmSuite {
  import Pulmonic.instances._
  import spire.std.int._

  checkAll("Eq[Pulmonic]", OrderLaws[Pulmonic].eqv)
  checkAll("MetricSpace[Pulmonic]", VectorSpaceLaws[Pulmonic, Int].metricSpace)
}
