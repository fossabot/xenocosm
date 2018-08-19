package xenocosm
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import galaxique.data.Point3
import squants.space.Meters

class CosmicLocationSpec extends xenocosm.test.XenocosmFunSuite {
  import CosmicLocation.instances._

  checkAll("Eq[CosmicLocation]", EqTests[CosmicLocation].eqv)

  test("CosmicLocation.distance.missing-coordinates") {
    val star1 = CosmicLocation(UUID.randomUUID(), Some(Point3.zero), Some(Point3.zero), None)
    val star2 = star1.copy(locS = None)

    (star1 distance star2) shouldBe Meters(0)
  }
}
