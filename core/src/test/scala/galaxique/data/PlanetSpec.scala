package galaxique
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import squants.space.{AstronomicalUnits, Parsecs}

class PlanetSpec extends xenocosm.test.XenocosmSuite {
  import Planet.instances._

  checkAll("Eq[Planet]", EqTests[Planet].eqv)

  test("Planet.proceeds.from.location") {
    val universe = Universe(UUID.randomUUID())
    val loc1 = Point3(Parsecs(1), Parsecs(1), Parsecs(1))
    val galaxy = Galaxy(universe, loc1)
    val loc2 = Point3(Parsecs(2), Parsecs(2), Parsecs(2))
    val star = Star(galaxy, loc2)
    val loc3 = Point3(AstronomicalUnits(3), AstronomicalUnits(3), AstronomicalUnits(3))
    val lhs = Planet(star, loc3)
    val rhs = Planet(star, loc3)

    lhs.radius shouldBe rhs.radius
    lhs.mass shouldBe rhs.mass
    lhs.semiMajorAxis shouldBe rhs.semiMajorAxis
  }
}
