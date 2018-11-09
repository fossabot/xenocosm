package xenocosm.json

import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.data.Trader

class TraderJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import trader._

  implicit val arb:Arbitrary[Trader] = Arbitrary(xenocosm.gen.trader)

  test("Trader.json.isomorphism") {
    forAll { a:Trader =>
      a.asJson.as[Trader] shouldBe Right(a)
    }
  }
}
