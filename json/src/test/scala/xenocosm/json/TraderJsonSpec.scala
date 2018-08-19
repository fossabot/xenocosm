package xenocosm.json

import io.circe.syntax._
import xenocosm.data.Trader

class TraderJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import Trader.instances._
  import trader._

  test("Trader.json.isomorphism") {
    forAll { (a:Trader) =>
      a.asJson.as[Trader] shouldBe Right(a)
    }
  }
}
