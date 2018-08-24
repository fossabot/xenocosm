package galaxique.json

import galaxique.MorganKeenan
import io.circe.syntax._
import org.scalacheck.Arbitrary

class MorganKeenanJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import morganKeenan._

  implicit val arb:Arbitrary[MorganKeenan] = Arbitrary(galaxique.gen.morganKeenan)

  test("MorganKeenan.json.isomorphism") {
    forAll { a:MorganKeenan =>
      a.asJson.as[MorganKeenan] shouldBe Right(a)
    }
  }
}
