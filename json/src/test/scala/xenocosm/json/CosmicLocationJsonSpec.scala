package xenocosm.json

import java.util.UUID
import galaxique.data.Point3
import io.circe.syntax._

import xenocosm.data.CosmicLocation

class CosmicLocationJsonSpec extends xenocosm.test.XenocosmSuite {
  import CosmicLocation.instances._
  import cosmicLocation._

  test("CosmicLocation.json.isomorphism") {
    forAll { a:CosmicLocation =>
      a.asJson.as[CosmicLocation] shouldBe Right(a)
    }
  }

  test("CosmicLocation.json.isomorphism.missing-locS") {
    val loc = CosmicLocation(UUID.randomUUID(), Some(Point3.zero), Some(Point3.zero), None)
    loc.asJson.as[CosmicLocation] shouldBe Right(loc)
  }

  test("CosmicLocation.json.isomorphism.missing-locG") {
    val loc = CosmicLocation(UUID.randomUUID(), Some(Point3.zero), None, None)
    loc.asJson.as[CosmicLocation] shouldBe Right(loc)
  }

  test("CosmicLocation.json.isomorphism.missing-locU") {
    val loc = CosmicLocation(UUID.randomUUID(), None, None, None)
    loc.asJson.as[CosmicLocation] shouldBe Right(loc)
  }
}
