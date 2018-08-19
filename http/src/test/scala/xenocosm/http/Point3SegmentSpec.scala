package xenocosm.http

import galaxique.data.Point3
import org.http4s.dsl.impl._
import squants.space.{AstronomicalUnits, Parsecs}

class Point3SegmentSpec extends xenocosm.test.XenocosmFunSuite {

  test("Point3Segment.path.unapply.intergalactic") {
    val loc1 = Point3(Parsecs(10000), Parsecs(10000), Parsecs(10000))
    val path = Path(s"/1,1,1")
    path match {
      case Root / ✺(loc2) => loc1 shouldBe loc2
      case _ => fail()
    }
  }

  test("Point3Segment.path.unapply.interstellar") {
    val loc1 = Point3(Parsecs(1), Parsecs(1), Parsecs(1))
    val path = Path(s"/1,1,1")
    path match {
      case Root / ✨(loc2) => loc1 shouldBe loc2
      case _ => fail()
    }
  }

  test("Point3Segment.path.unapply.interplanetary") {
    val loc1 = Point3(AstronomicalUnits(1), AstronomicalUnits(1), AstronomicalUnits(1))
    val path = Path(s"/1,1,1")
    path match {
      case Root / ★(loc2) => loc1 shouldBe loc2
      case _ => fail()
    }
  }
}
