package xenocosm
package data

import cats.kernel.laws.discipline.{EqTests, MonoidTests}
import org.scalacheck.Arbitrary

class ElapsedTimeSpec extends xenocosm.test.XenocosmFunSuite {
  import ElapsedTime.instances._

  implicit val arb:Arbitrary[ElapsedTime] = Arbitrary(gen.elapsedTime)

  checkAll("Eq[ElapsedTime]", EqTests[ElapsedTime].eqv)
  checkAll("Monoid[ElapsedTime]", MonoidTests[ElapsedTime].monoid)
}
