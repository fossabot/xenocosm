package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Roundedness

class RoundednessJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import roundedness._

  implicit val arb:Arbitrary[Roundedness] = Arbitrary(pseudoglot.gen.roundedness)

  test("Roundedness.json.isomorphism") {
    forAll { a:Roundedness =>
      a.asJson.as[Roundedness] shouldBe Right(a)
    }
  }
}
