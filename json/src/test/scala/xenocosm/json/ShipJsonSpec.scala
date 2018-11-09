package xenocosm.json

import xenocosm.data.Ship
import io.circe.syntax._
import org.scalacheck.Arbitrary

class ShipJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import ship._

  implicit val arb:Arbitrary[Ship] = Arbitrary(xenocosm.gen.ship)

  test("Ship.json.isomorphism") {
    forAll { a:Ship =>
      a.asJson.as[Ship] shouldBe Right(a)
    }
  }
}
