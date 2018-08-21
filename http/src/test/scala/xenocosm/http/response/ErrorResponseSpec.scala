package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}
import squants.space.Parsecs

import xenocosm.{NoMovesRemaining, TooFar}

class ErrorResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import ErrorResponse.instances._

  val gen:Gen[ErrorResponse] =
    for {
      distance <- Gen.posNum[Int].map(Parsecs[Int])
      error <- Gen.oneOf(NoMovesRemaining, TooFar(distance))
    } yield ErrorResponse(error)

  implicit val arb:Arbitrary[ErrorResponse] = Arbitrary(gen)

  test("ErrorResponse.json.isomorphism") {
    forAll { a:ErrorResponse =>
      a.asJson.as[ErrorResponse] shouldBe Right(a)
    }
  }
}
