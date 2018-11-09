package galaxique.json

import io.circe.syntax._
import galaxique.data.Galaxy
import org.scalacheck.Arbitrary

class GalaxyJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import galaxy._

  implicit val arb:Arbitrary[Galaxy] = Arbitrary(galaxique.gen.galaxy)

  test("Galaxy.json.isomorphism") {
    forAll { a:Galaxy =>
      a.asJson.as[Galaxy] shouldBe Right(a)
    }
  }
}
