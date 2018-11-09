package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Height

class HeightJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import height._

  implicit val arb:Arbitrary[Height] = Arbitrary(pseudoglot.gen.height)

  test("Height.json.isomorphism") {
    forAll { a:Height =>
      a.asJson.as[Height] shouldBe Right(a)
    }
  }
}
