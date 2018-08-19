package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import spire.laws._
import spire.std.int._

class PhoneSpec extends xenocosm.test.XenocosmFunSuite {
  import Phone.instances._

  checkAll("Eq[Pulmonic]", EqTests[Pulmonic].eqv)
  checkAll("Eq[Vowel]", EqTests[Vowel].eqv)
  checkAll("MetricSpace[Pulmonic]", VectorSpaceLaws[Pulmonic, Int].metricSpace)
  checkAll("MetricSpace[Vowel]", VectorSpaceLaws[Vowel, Int].metricSpace)
}
