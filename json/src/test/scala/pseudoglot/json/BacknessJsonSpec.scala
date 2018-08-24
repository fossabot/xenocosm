package pseudoglot.json

import io.circe.syntax._
import org.scalacheck.Arbitrary
import pseudoglot.data.Backness

class BacknessJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import backness._

  implicit val arb:Arbitrary[Backness] = Arbitrary(pseudoglot.gen.backness)

  test("Backness.json.isomorphism") {
    forAll { a:Backness =>
      a.asJson.as[Backness] shouldBe Right(a)
    }
  }
}
