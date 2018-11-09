package galaxique
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import squants.space.Parsecs

class StarSpec extends xenocosm.test.XenocosmFunSuite {
  import Star.instances._
  import SparseSpace3.syntax._

  implicit val arbPoint3:Arbitrary[Point3] = Arbitrary(gen.point3)
  implicit val arbStar:Arbitrary[Star] = Arbitrary(gen.star)

  checkAll("Eq[Star]", EqTests[Star].eqv)

  test("Star.proceeds.from.location") {
    val universe = Universe(UUID.randomUUID())
    val loc1 = Point3(Parsecs(1), Parsecs(1), Parsecs(1))
    val galaxy = Galaxy(universe, loc1)
    val loc2 = Point3(Parsecs(2), Parsecs(2), Parsecs(2))
    val lhs = Star(galaxy, loc2)
    val rhs = Star(galaxy, loc2)

    lhs.mk shouldBe rhs.mk
    lhs.mass shouldBe rhs.mass
    lhs.luminosity shouldBe rhs.luminosity
    lhs.radius shouldBe rhs.radius
    lhs.temperature shouldBe rhs.temperature
  }

  test("Star.SparseSpace3") {
    forAll { (star:Star, loc:Point3) =>
      star.locate(loc) match {
        case Some(Planet(x, y)) =>
          x shouldBe star
          y shouldBe loc
        case None => succeed
      }
    }
  }
}
