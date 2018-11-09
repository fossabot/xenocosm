package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}
import squants.space.Meters

class StarResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import StarResponse.instances._

  implicit val arb:Arbitrary[StarResponse] =
    Arbitrary {
      for {
        star <- galaxique.gen.star
        loc <- galaxique.gen.point3
        range <- Gen.posNum[Double].map(Meters.apply[Double])
      } yield StarResponse(star, loc, range)
    }

  test("StarResponse.json.isomorphism") {
    forAll { a:StarResponse =>
      a.asJson.as[StarResponse] shouldBe Right(a)
    }
  }
}
