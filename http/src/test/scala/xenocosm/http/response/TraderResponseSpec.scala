package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.Arbitrary

class TraderResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import TraderResponse.instances._

  implicit val arb:Arbitrary[TraderResponse] =
    Arbitrary {
      for {
        trader <- xenocosm.gen.trader
      } yield TraderResponse(trader)
    }

  test("TraderResponse.json.isomorphism") {
    forAll { a:TraderResponse =>
      a.asJson.as[TraderResponse] shouldBe Right(a)
    }
  }
}
