package xenocosm.http
package response

import io.circe.syntax._

class StarResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import StarResponse.instances._

  test("StarResponse.json.isomorphism") {
    forAll { a:StarResponse =>
      a.asJson.as[StarResponse] shouldBe Right(a)
    }
  }
}
