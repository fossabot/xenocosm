package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Backness

class BacknessJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Backness.instances._
  import backness._

  test("Backness.json.isomorphism") {
    forAll { (a:Backness) =>
      a.asJson.as[Backness] shouldBe Right(a)
    }
  }
}
