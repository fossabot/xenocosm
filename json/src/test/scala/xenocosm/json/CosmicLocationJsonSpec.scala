package xenocosm.json

import java.util.UUID
import galaxique.data.Point3
import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.data.CosmicLocation

class CosmicLocationJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import cosmicLocation._

  implicit val arb:Arbitrary[CosmicLocation] = Arbitrary(xenocosm.gen.cosmicLocation)

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
