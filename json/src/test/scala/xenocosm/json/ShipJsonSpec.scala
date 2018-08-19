package xenocosm.json

import xenocosm.data.Ship
import io.circe.syntax._

class ShipJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Ship.instances._
  import ship._

  test("Ship.json.isomorphism") {
    forAll { (a:Ship) =>
      a.asJson.as[Ship] shouldBe Right(a)
    }
  }
}
