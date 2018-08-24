package xenocosm.json

import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.data.Identity

class IdentityJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import identity._

  implicit val arb:Arbitrary[Identity] = Arbitrary(xenocosm.gen.identity)

  test("Identity.json.isomorphism") {
    forAll { a:Identity =>
      a.asJson.as[Identity] shouldBe Right(a)
    }
  }
}
