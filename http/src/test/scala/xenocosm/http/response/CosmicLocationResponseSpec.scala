package xenocosm.http
package response

import io.circe.syntax._
import org.scalacheck.Arbitrary

class CosmicLocationResponseSpec extends xenocosm.test.XenocosmFunSuite {
  import CosmicLocationResponse.instances._

  implicit val arb:Arbitrary[CosmicLocationResponse] =
    Arbitrary(xenocosm.gen.cosmicLocation.map(CosmicLocationResponse.apply))

  test("CosmicLocationResponse.json.isomorphism") {
    forAll { a:CosmicLocationResponse =>
      a.asJson.as[CosmicLocationResponse] shouldBe Right(a)
    }
  }
}
