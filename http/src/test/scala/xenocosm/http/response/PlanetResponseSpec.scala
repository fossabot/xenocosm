package xenocosm.http
package response

import io.circe.syntax._

class PlanetResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import PlanetResponse.instances._

  test("PlanetResponse.json.isomorphism") {
    forAll { a:PlanetResponse =>
      a.asJson.as[PlanetResponse] shouldBe Right(a)
    }
  }
}
