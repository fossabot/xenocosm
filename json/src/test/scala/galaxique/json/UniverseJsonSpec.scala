package galaxique.json

import io.circe.syntax._
import galaxique.data.Universe

class UniverseJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Universe.instances._
  import universe._

  test("Universe.json.isomorphism") {
    forAll { (a:Universe) =>
      a.asJson.as[Universe] shouldBe Right(a)
    }
  }
}
