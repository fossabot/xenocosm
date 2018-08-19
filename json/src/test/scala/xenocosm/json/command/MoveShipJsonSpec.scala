package xenocosm.json
package command

import io.circe.syntax._
import spire.random.Dist

import xenocosm.MoveShip
import xenocosm.data.{CosmicLocation, Ship}

class MoveShipJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import instances._
  import CosmicLocation.instances._
  import Ship.instances._

  implicit val moveShipDist:Dist[MoveShip] =
    for {
      moves <- Dist.uint
      ship <- Dist[Ship]
      loc <- Dist[CosmicLocation]
    } yield MoveShip(moves, ship, loc)

  test("MoveShip.json.isomorphism") {
    forAll { a:MoveShip =>
      a.asJson.as[MoveShip] shouldBe Right(a)
    }
  }
}
