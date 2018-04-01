package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Roundedness

class RoundednessJsonSpec extends xenocosm.test.XenocosmSuite {
  import Roundedness.instances._
  import roundedness._

  test("Roundedness.json.isomorphism") {
    forAll { (a:Roundedness) =>
      a.asJson.as[Roundedness] shouldBe Right(a)
    }
  }
}
