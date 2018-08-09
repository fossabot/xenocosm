package xenocosm.http
package response

import io.circe.syntax._

class TraderResponseSpec extends xenocosm.test.XenocosmSuite {
  import TraderResponse.instances._

  test("TraderResponse.json.isomorphism") {
    forAll { a:TraderResponse =>
      a.asJson.as[TraderResponse] shouldBe Right(a)
    }
  }
}
