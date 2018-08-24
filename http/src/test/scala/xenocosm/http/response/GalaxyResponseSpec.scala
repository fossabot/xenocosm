package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.{Arbitrary, Gen}
import squants.space.Meters

class GalaxyResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import GalaxyResponse.instances._

  implicit val arb:Arbitrary[GalaxyResponse] =
    Arbitrary {
      for {
        galaxy <- galaxique.gen.galaxy
        loc <- galaxique.gen.point3
        range <- Gen.posNum[Double].map(Meters.apply[Double])
      } yield GalaxyResponse(galaxy, loc, range)
    }

  test("GalaxyResponse.json.isomorphism") {
    forAll { a:GalaxyResponse =>
      a.asJson.as[GalaxyResponse] shouldBe Right(a)
    }
  }
}
