package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.Point3
import org.scalacheck.Arbitrary
import spire.random.{Dist, Random, Seed}
import squants.space.Meters

class CosmicLocationSpec extends xenocosm.test.XenocosmFunSuite {
  import CosmicLocation.instances._

  implicit val arbCosmicLocation:Arbitrary[CosmicLocation] = Arbitrary(gen.cosmicLocation)
  implicit val arbSeed:Arbitrary[Seed] = Arbitrary(interop.gen.seed)

  checkAll("Eq[CosmicLocation]", EqTests[CosmicLocation].eqv)

  test("generated location has a planet") {
    forAll { seed:Seed =>
      val gen = Random.generatorFromSeed(seed)
      val loc = implicitly[Dist[CosmicLocation]].apply(gen)

      loc.planet should not be empty
    }
  }

  test("CosmicLocation.distance.missing-coordinates") {
    val star1 = CosmicLocation(UUID.randomUUID(), Some(Point3.zero), Some(Point3.zero), None)
    val star2 = star1.copy(locS = None)

    (star1 distance star2) shouldBe Meters(0)
  }
}
