package xenocosm.json

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}
import spire.math.UInt

import xenocosm.data.{ForeignID, Identity}

class IdentityJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import identity._

  val gen:Gen[Identity] = for {
    uuid <- Gen.uuid
    ref <- Gen.option(Gen.alphaNumStr.map(ForeignID.apply))
    moves <- Gen.posNum[Int].map(UInt.apply)
  } yield Identity(uuid, ref, moves)

  implicit val arb:Arbitrary[Identity] = Arbitrary(gen)

  test("Identity.json.isomorphism") {
    forAll { a:Identity =>
      a.asJson.as[Identity] shouldBe Right(a)
    }
  }
}
