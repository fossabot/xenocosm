package galaxique
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import squants.space.Parsecs

class GalaxySpec extends xenocosm.test.XenocosmFunSuite {
  import Point3.instances._
  import Galaxy.instances._
  import SparseSpace3.syntax._

  checkAll("Eq[Galaxy]", EqTests[Galaxy].eqv)

  test("Galaxy.proceeds.from.location") {
    val universe = Universe(UUID.randomUUID())
    val loc = Point3(Parsecs(1), Parsecs(1), Parsecs(1))
    val lhs = Galaxy(universe, loc)
    val rhs = Galaxy(universe, loc)

    lhs.luminosity shouldBe rhs.luminosity
    lhs.diameter shouldBe rhs.diameter
    lhs.mass shouldBe rhs.mass
  }

  test("Galaxy.SparseSpace3") {
    forAll { (galaxy:Galaxy, loc:Point3) =>
      galaxy.locate(loc) match {
        case Some(Star(x, y)) =>
          x shouldBe galaxy
          y shouldBe loc
        case None => succeed
      }
    }
  }
}
