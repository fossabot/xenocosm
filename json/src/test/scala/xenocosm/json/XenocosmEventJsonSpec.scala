package xenocosm.json

import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.XenocosmEvent

class XenocosmEventJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import xenocosmEvent._

  implicit val arb:Arbitrary[XenocosmEvent] = Arbitrary(xenocosm.gen.event)

  test("XenocosmEvent.json.isomorphism") {
    forAll { a:XenocosmEvent =>
      a.asJson.as[XenocosmEvent] shouldBe Right(a)
    }
  }
}
