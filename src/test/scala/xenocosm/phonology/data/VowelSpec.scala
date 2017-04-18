package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.laws._

class VowelSpec extends XenocosmSuite {
  import Vowel.instances._
  import spire.std.int._

  checkAll("Eq[Vowel]", OrderLaws[Vowel].eqv)
  checkAll("MetricSpace[Vowel]", VectorSpaceLaws[Vowel, Int].metricSpace)
}
