package xenocosm.json
package command

import io.circe.syntax._
import spire.random.Dist

import xenocosm.CreateTrader

class CreateTraderJsonSpec extends xenocosm.test.XenocosmSuite {
  import instances._

  implicit val createTraderDist:Dist[CreateTrader] =
    for {
      moves <- Dist.uint
    } yield CreateTrader(moves)

  test("CreateTrader.json.isomorphism") {
    forAll { a:CreateTrader =>
      a.asJson.as[CreateTrader] shouldBe Right(a)
    }
  }
}
