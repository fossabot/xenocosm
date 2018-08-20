package xenocosm.json

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}

import xenocosm.data.ForeignID

class ForeignIDJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import foreignID._

  implicit val arb:Arbitrary[ForeignID] = Arbitrary(Gen.alphaNumStr.map(ForeignID.apply))

  test("ForeignID.json.isomorphism") {
    forAll { a:ForeignID =>
      a.asJson.as[ForeignID] shouldBe Right(a)
    }
  }
}
