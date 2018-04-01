package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Phonology

class PhonologyJsonSpec extends xenocosm.test.XenocosmSuite {
  import Phonology.instances._
  import phonology._

  test("Phonology.json.isomorphism") {
    forAll { (a:Phonology) =>
      a.asJson.as[Phonology] shouldBe Right(a)
    }
  }
}
