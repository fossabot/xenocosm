package xenocosm.json

import io.circe.syntax._
import xenocosm.data.CosmicLocation

class CosmicLocationJsonSpec extends xenocosm.test.XenocosmSuite {
  import CosmicLocation.instances._
  import cosmicLocation._

  test("CosmicLocation.json.isomorphism") {
    forAll { (a:CosmicLocation) =>
      a.asJson.as[CosmicLocation] shouldBe Right(a)
    }
  }
}
