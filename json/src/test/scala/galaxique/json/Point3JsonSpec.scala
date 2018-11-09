package galaxique.json

import io.circe.syntax._
import galaxique.data.Point3
import org.scalacheck.Arbitrary

class Point3JsonSpec extends xenocosm.test.XenocosmFunSuite {
  import point3._

  implicit val arb:Arbitrary[Point3] = Arbitrary(galaxique.gen.point3)

  test("Point3.json.isomorphism") {
    forAll { a:Point3 =>
      a.asJson.as[Point3] shouldBe Right(a)
    }
  }
}
