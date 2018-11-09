package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Phonology

class PhonologyJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import phonology._

  implicit val arb:Arbitrary[Phonology] = Arbitrary(pseudoglot.gen.phonology)

  test("Phonology.json.isomorphism") {
    forAll { a:Phonology =>
      a.asJson.as[Phonology] shouldBe Right(a)
    }
  }
}
