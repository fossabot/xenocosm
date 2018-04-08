package xenocosm.http
package data

import io.circe.syntax._

class MultiverseResponseSpec extends xenocosm.test.XenocosmSuite {
  import MultiverseResponse.instances._

  test("MultiverseResponse.json.isomorphism") {
    forAll { (a:MultiverseResponse.type) =>
      a.asJson.as[MultiverseResponse.type] shouldBe Right(a)
    }
  }
}
