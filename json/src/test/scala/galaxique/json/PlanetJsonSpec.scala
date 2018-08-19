package galaxique.json

import io.circe.syntax._
import galaxique.data.Planet

class PlanetJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Planet.instances._
  import planet._

  test("Planet.json.isomorphism") {
    forAll { (a:Planet) =>
      a.asJson.as[Planet] shouldBe Right(a)
    }
  }
}
