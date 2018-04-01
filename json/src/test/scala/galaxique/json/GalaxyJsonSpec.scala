package galaxique.json

import io.circe.syntax._
import galaxique.data.Galaxy

class GalaxyJsonSpec extends xenocosm.test.XenocosmSuite {
  import Galaxy.instances._
  import galaxy._

  test("Galaxy.json.isomorphism") {
    forAll { (a:Galaxy) =>
      a.asJson.as[Galaxy] shouldBe Right(a)
    }
  }
}
