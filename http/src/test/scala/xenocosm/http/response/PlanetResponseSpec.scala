package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.Arbitrary

class PlanetResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import PlanetResponse.instances._

  implicit val arb:Arbitrary[PlanetResponse] =
    Arbitrary {
      for {
        planet <- galaxique.gen.planet
      } yield PlanetResponse(planet)
    }

  test("PlanetResponse.json.isomorphism") {
    forAll { a:PlanetResponse =>
      a.asJson.as[PlanetResponse] shouldBe Right(a)
    }
  }
}
