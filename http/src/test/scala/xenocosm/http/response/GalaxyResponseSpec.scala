package xenocosm.http
package response

import io.circe.syntax._

class GalaxyResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import GalaxyResponse.instances._

  test("GalaxyResponse.json.isomorphism") {
    forAll { a:GalaxyResponse =>
      a.asJson.as[GalaxyResponse] shouldBe Right(a)
    }
  }
}
