package galaxique.json

import io.circe.syntax._
import galaxique.data.Planet
import org.scalacheck.Arbitrary

class PlanetJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import planet._

  implicit val arb:Arbitrary[Planet] = Arbitrary(galaxique.gen.planet)

  test("Planet.json.isomorphism") {
    forAll { a:Planet =>
      a.asJson.as[Planet] shouldBe Right(a)
    }
  }
}
