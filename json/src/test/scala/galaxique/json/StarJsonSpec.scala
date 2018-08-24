package galaxique.json

import io.circe.syntax._
import galaxique.data.Star
import org.scalacheck.Arbitrary

class StarJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import star._

  implicit val arb:Arbitrary[Star] = Arbitrary(galaxique.gen.star)

  test("Star.json.isomorphism") {
    forAll { a:Star =>
      a.asJson.as[Star] shouldBe Right(a)
    }
  }
}
