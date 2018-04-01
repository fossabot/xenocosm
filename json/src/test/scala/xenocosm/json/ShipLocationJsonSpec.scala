package xenocosm.json

import io.circe.syntax._
import xenocosm.data.ShipLocation

class ShipLocationJsonSpec extends xenocosm.test.XenocosmSuite {
  import ShipLocation.instances._
  import shipLocation._

  test("ShipLocation.json.isomorphism") {
    forAll { (a:ShipLocation) =>
      a.asJson.as[ShipLocation] shouldBe Right(a)
    }
  }
}
