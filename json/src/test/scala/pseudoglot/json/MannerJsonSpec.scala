package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Manner

class MannerJsonSpec extends xenocosm.test.XenocosmSuite {
  import Manner.instances._
  import manner._

  test("Manner.json.isomorphism") {
    forAll { (a:Manner) =>
      a.asJson.as[Manner] shouldBe Right(a)
    }
  }
}
