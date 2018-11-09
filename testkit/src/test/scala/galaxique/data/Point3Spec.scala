package galaxique
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.laws._
import squants.space.{Length, Parsecs}

class Point3Spec extends xenocosm.test.XenocosmFunSuite {
  import interop.squants.instances._
  import Point3.instances._

  implicit val arbLength:Arbitrary[Length] = Arbitrary(interop.gen.length)
  implicit val arbPoint3:Arbitrary[Point3] = Arbitrary(galaxique.gen.point3)

  checkAll("Eq[Point3]", EqTests[Point3].eqv)
  checkAll("MetricSpace[Point3]", VectorSpaceLaws[Point3, Length].metricSpace)

  test("Given a Point3, generate integer points in a cube") {
    val p3 = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
    val points = Point3.wholePointsInCube(Parsecs(4), Parsecs(2), p3).toVector

    points should contain (p3)
    points.size should be (27)
  }
}
