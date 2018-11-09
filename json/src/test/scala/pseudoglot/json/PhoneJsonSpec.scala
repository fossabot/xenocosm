package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.{Pulmonic, Vowel}

class PhoneJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import pulmonic._
  import vowel._

  implicit val arbPulmonic:Arbitrary[Pulmonic] = Arbitrary(pseudoglot.gen.pulmonic)
  implicit val arbVowel:Arbitrary[Vowel] = Arbitrary(pseudoglot.gen.vowel)

  test("Pulmonic.json.isomorphism") {
    forAll { a:Pulmonic =>
      a.asJson.as[Pulmonic] shouldBe Right(a)
    }
  }

  test("Vowel.json.isomorphism") {
    forAll { a:Vowel =>
      a.asJson.as[Vowel] shouldBe Right(a)
    }
  }
}
