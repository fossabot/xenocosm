package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Manner

class MannerJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import manner._

  implicit val arb:Arbitrary[Manner] = Arbitrary(pseudoglot.gen.manner)

  test("Manner.json.isomorphism") {
    forAll { a:Manner =>
      a.asJson.as[Manner] shouldBe Right(a)
    }
  }
}
