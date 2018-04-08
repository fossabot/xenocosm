package xenocosm.http
package data

import io.circe.syntax._

class GalaxyResponseSpec extends xenocosm.test.XenocosmSuite {
  import GalaxyResponse.instances._

  test("GalaxyResponse.json.isomorphism") {
    forAll { (a:GalaxyResponse) =>
      a.asJson.as[GalaxyResponse] shouldBe Right(a)
    }
  }
}
