package xenocosm.json

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}
import squants.space.Parsecs

import xenocosm.{NoMovesRemaining, TooFar, XenocosmError}

class XenocosmErrorJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import xenocosmError._

  val gen:Gen[XenocosmError] =
    for {
      distance <- Gen.posNum[Int].map(Parsecs[Int])
      error <- Gen.oneOf(NoMovesRemaining, TooFar(distance))
    } yield error

  implicit val arb:Arbitrary[XenocosmError] = Arbitrary(gen)

  test("XenocosmError.json.isomorphism") {
    forAll { a:XenocosmError =>
      a.asJson.as[XenocosmError] shouldBe Right(a)
    }
  }
}
