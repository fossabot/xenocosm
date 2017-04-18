package xenocosm
package geometry
package data

import cats.kernel.laws.OrderLaws
import spire.laws._
import squants.space.{Length, Parsecs}

class Point3Spec extends XenocosmSuite {
  import Point3.instances._
  import xenocosm.instances.interop._

  checkAll("Eq[Point3]", OrderLaws[Point3].eqv)
  checkAll("MetricSpace[Point3]", VectorSpaceLaws[Point3, Length].metricSpace)

  test("Given a Point3, generate integer points in a cube") {
    val p3 = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
    val points = Point3.wholePointsInCube(Parsecs(1), p3)

    points.toVector.size should be (27)
  }
}
