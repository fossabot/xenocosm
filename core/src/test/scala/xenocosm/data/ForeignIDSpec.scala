package xenocosm
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.{Arbitrary, Gen}

class ForeignIDSpec extends xenocosm.test.XenocosmFunSuite {
  import ForeignID.instances._

  val genForeignID:Gen[ForeignID] = Gen.alphaStr.map(ForeignID.apply)

  private implicit val arb:Arbitrary[ForeignID] = Arbitrary(genForeignID)

  checkAll("Eq[ForeignID]", EqTests[ForeignID].eqv)
}
