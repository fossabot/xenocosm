package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.Arbitrary

class ErrorResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import ErrorResponse.instances._

  implicit val arb:Arbitrary[ErrorResponse] =
    Arbitrary {
      for {
        error <- xenocosm.gen.error
      } yield ErrorResponse(error)
    }

  test("ErrorResponse.json.isomorphism") {
    forAll { a:ErrorResponse =>
      a.asJson.as[ErrorResponse] shouldBe Right(a)
    }
  }
}
