package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Voicing

class VoicingJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import voicing._

  implicit val arb:Arbitrary[Voicing] = Arbitrary(pseudoglot.gen.voicing)

  test("Voicing.json.isomorphism") {
    forAll { a:Voicing =>
      a.asJson.as[Voicing] shouldBe Right(a)
    }
  }
}
