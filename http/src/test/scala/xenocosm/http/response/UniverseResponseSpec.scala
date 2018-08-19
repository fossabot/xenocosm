package xenocosm.http
package response

import io.circe.syntax._

class UniverseResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import UniverseResponse.instances._

  test("UniverseResponse.json.isomorphism") {
    forAll { a:UniverseResponse =>
      a.asJson.as[UniverseResponse] shouldBe Right(a)
    }
  }
}
