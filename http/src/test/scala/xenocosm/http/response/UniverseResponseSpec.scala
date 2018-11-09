package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}
import squants.space.Meters

class UniverseResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import UniverseResponse.instances._

  implicit val arb:Arbitrary[UniverseResponse] =
    Arbitrary {
      for {
        universe <- galaxique.gen.universe
        loc <- galaxique.gen.point3
        range <- Gen.posNum[Double].map(Meters.apply[Double])
      } yield UniverseResponse(universe, loc, range)
    }

  test("UniverseResponse.json.isomorphism") {
    forAll { a:UniverseResponse =>
      a.asJson.as[UniverseResponse] shouldBe Right(a)
    }
  }
}
