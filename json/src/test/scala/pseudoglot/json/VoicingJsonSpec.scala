package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Voicing

class VoicingJsonSpec extends xenocosm.test.XenocosmSuite {
  import Voicing.instances._
  import voicing._

  test("Voicing.json.isomorphism") {
    forAll { (a:Voicing) =>
      a.asJson.as[Voicing] shouldBe Right(a)
    }
  }
}
