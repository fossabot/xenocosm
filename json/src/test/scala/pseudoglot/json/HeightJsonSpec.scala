package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.Height

class HeightJsonSpec extends xenocosm.test.XenocosmSuite {
  import Height.instances._
  import height._

  test("Height.json.isomorphism") {
    forAll { (a:Height) =>
      a.asJson.as[Height] shouldBe Right(a)
    }
  }
}
