package galaxique.json

import io.circe.syntax._
import galaxique.data.Star

class StarJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Star.instances._
  import star._

  test("Star.json.isomorphism") {
    forAll { (a:Star) =>
      a.asJson.as[Star] shouldBe Right(a)
    }
  }
}
