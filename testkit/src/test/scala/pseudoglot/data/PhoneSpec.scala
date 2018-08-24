package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.laws._
import spire.std.int._

class PhoneSpec extends xenocosm.test.XenocosmFunSuite {
  import Phone.instances._

  implicit val arbPulmonic:Arbitrary[Pulmonic] = Arbitrary(pseudoglot.gen.pulmonic)
  implicit val arbVowel:Arbitrary[Vowel] = Arbitrary(pseudoglot.gen.vowel)

  checkAll("Eq[Pulmonic]", EqTests[Pulmonic].eqv)
  checkAll("Eq[Vowel]", EqTests[Vowel].eqv)
  checkAll("MetricSpace[Pulmonic]", VectorSpaceLaws[Pulmonic, Int].metricSpace)
  checkAll("MetricSpace[Vowel]", VectorSpaceLaws[Vowel, Int].metricSpace)
}
