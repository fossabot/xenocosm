package xenocosm
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.{Arbitrary, Gen}
import spire.math.UInt

class IdentitySpec extends xenocosm.test.XenocosmFunSuite {
  import Identity.instances._

  val genIdentity:Gen[Identity] =
    for {
      uuid <- Gen.uuid
      ref <- Gen.option(Gen.alphaStr.map(ForeignID.apply))
      moves <- Gen.posNum[Int].map(UInt.apply)
    } yield Identity(uuid, ref, moves)

  private implicit val arb:Arbitrary[Identity] = Arbitrary(genIdentity)

  checkAll("Eq[Identity]", EqTests[Identity].eqv)
}
