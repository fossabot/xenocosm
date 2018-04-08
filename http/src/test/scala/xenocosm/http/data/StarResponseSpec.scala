package xenocosm.http
package data

import io.circe.syntax._

class StarResponseSpec extends xenocosm.test.XenocosmSuite {
  import StarResponse.instances._

  test("StarResponse.json.isomorphism") {
    forAll { (a:StarResponse) =>
      a.asJson.as[StarResponse] shouldBe Right(a)
    }
  }
}
