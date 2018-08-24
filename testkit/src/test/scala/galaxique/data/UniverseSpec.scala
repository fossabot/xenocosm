package galaxique
package data

import java.util.UUID
import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary

class UniverseSpec extends xenocosm.test.XenocosmFunSuite {
  import Universe.instances._
  import SparseSpace3.syntax._

  implicit val arbPoint3:Arbitrary[Point3] = Arbitrary(gen.point3)
  implicit val arbUniverse:Arbitrary[Universe] = Arbitrary(gen.universe)

  checkAll("Eq[Universe]", EqTests[Universe].eqv)

  test("Universe.proceeds.from.UUID") {
    val uuid = UUID.randomUUID()
    val lhs = Universe(uuid)
    val rhs = Universe(uuid)

    lhs.age shouldBe rhs.age
    lhs.diameter shouldBe rhs.diameter
  }

  test("Universe.SparseSpace3") {
    forAll { (universe:Universe, loc:Point3) =>
      universe.locate(loc) match {
        case Some(Galaxy(x, y)) =>
          x shouldBe universe
          y shouldBe loc
        case None => succeed
      }
    }
  }
}
