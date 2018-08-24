package xenocosm.json

import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.XenocosmError

class XenocosmErrorJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import xenocosmError._

  implicit val arb:Arbitrary[XenocosmError] = Arbitrary(xenocosm.gen.error)

  test("XenocosmError.json.isomorphism") {
    forAll { a:XenocosmError =>
      a.asJson.as[XenocosmError] shouldBe Right(a)
    }
  }
}
