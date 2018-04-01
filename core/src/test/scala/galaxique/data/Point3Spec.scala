package galaxique
package data

import cats.kernel.laws.discipline.PartialOrderTests
import cats.implicits._
import spire.laws._
import squants.space.{Length, Parsecs}

class Point3Spec extends xenocosm.test.XenocosmSuite {
  import Point3.instances._
  import interop.length._

  checkAll("PartialOrder[Point3]", PartialOrderTests[Point3].partialOrder)
  checkAll("MetricSpace[Point3]", VectorSpaceLaws[Point3, Length].metricSpace)

  test("Given a Point3, generate integer points in a cube") {
    val p3 = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
    val points = Point3.wholePointsInCube(Parsecs(4), Parsecs(2), p3).toVector

    points should contain (p3)
    points.size should be (27)
  }
}
