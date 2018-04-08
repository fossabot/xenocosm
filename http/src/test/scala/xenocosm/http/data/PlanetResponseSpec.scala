package xenocosm.http
package data

import io.circe.syntax._

class PlanetResponseSpec extends xenocosm.test.XenocosmSuite {
  import PlanetResponse.instances._

  test("PlanetResponse.json.isomorphism") {
    forAll { (a:PlanetResponse) =>
      a.asJson.as[PlanetResponse] shouldBe Right(a)
    }
  }
}