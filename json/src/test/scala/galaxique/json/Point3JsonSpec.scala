package galaxique.json

import io.circe.syntax._
import galaxique.data.Point3

class Point3JsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Point3.instances._
  import point3._

  test("Point3.json.isomorphism") {
    forAll { (a:Point3) =>
      a.asJson.as[Point3] shouldBe Right(a)
    }
  }
}
