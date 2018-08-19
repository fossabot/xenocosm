package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Place

class PlaceJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Place.instances._
  import place._

  test("Place.json.isomorphism") {
    forAll { (a:Place) =>
      a.asJson.as[Place] shouldBe Right(a)
    }
  }
}
