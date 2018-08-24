package galaxique.json

import io.circe.syntax._
import galaxique.data.Universe
import org.scalacheck.Arbitrary

class UniverseJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import universe._

  implicit val arb:Arbitrary[Universe] = Arbitrary(galaxique.gen.universe)

  test("Universe.json.isomorphism") {
    forAll { a:Universe =>
      a.asJson.as[Universe] shouldBe Right(a)
    }
  }
}
