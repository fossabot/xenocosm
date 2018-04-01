package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.{Phone, Pulmonic, Vowel}

class PhoneJsonSpec extends xenocosm.test.XenocosmSuite {
  import Phone.instances._
  import pulmonic._
  import vowel._

  test("Pulmonic.json.isomorphism") {
    forAll { (a:Pulmonic) =>
      a.asJson.as[Pulmonic] shouldBe Right(a)
    }
  }

  test("Vowel.json.isomorphism") {
    forAll { (a:Vowel) =>
      a.asJson.as[Vowel] shouldBe Right(a)
    }
  }
}
