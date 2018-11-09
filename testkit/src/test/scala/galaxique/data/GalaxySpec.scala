package galaxique
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import squants.space.Parsecs

class GalaxySpec extends xenocosm.test.XenocosmFunSuite {
  import Galaxy.instances._
  import SparseSpace3.syntax._

  implicit val arbPoint3:Arbitrary[Point3] = Arbitrary(gen.point3)
  implicit val arbGalaxy:Arbitrary[Galaxy] = Arbitrary(gen.galaxy)

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
