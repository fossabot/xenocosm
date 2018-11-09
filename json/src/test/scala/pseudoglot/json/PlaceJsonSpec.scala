package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Place

class PlaceJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import place._

  implicit val arb:Arbitrary[Place] = Arbitrary(pseudoglot.gen.place)

  test("Place.json.isomorphism") {
    forAll { a:Place =>
      a.asJson.as[Place] shouldBe Right(a)
    }
  }
}
