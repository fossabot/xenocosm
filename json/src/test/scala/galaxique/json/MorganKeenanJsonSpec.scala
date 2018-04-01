package galaxique.json

import galaxique.MorganKeenan
import io.circe.syntax._

class MorganKeenanJsonSpec extends xenocosm.test.XenocosmSuite {
  import MorganKeenan.instances._
  import morganKeenan._

  test("MorganKeenan.json.isomorphism") {
    forAll { (a:MorganKeenan) =>
      a.asJson.as[MorganKeenan] shouldBe Right(a)
    }
  }
}
