package xenocosm.http
package data

import io.circe.syntax._

class UniverseResponseSpec extends xenocosm.test.XenocosmSuite {
  import UniverseResponse.instances._

  test("UniverseResponse.json.isomorphism") {
    forAll { (a:UniverseResponse) =>
      a.asJson.as[UniverseResponse] shouldBe Right(a)
    }
  }
}
