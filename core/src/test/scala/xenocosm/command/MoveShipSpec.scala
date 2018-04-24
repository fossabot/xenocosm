package xenocosm
package command

import cats.data.{NonEmptyList, Validated}

import xenocosm.data.{CosmicLocation, Moves, Ship}
import xenocosm.error.NoMovesRemaining

class MoveShipSpec extends xenocosm.test.XenocosmSuite {
  import CosmicLocation.instances._
  import Ship.instances._

  test("MoveShip.valid") {
    forAll { (ship:Ship, loc:CosmicLocation) =>
      val cmd = MoveShip(Moves(1), ship, loc)
      MoveShip.validate(cmd).isValid shouldBe true
    }
  }

  test("MoveShip.invalid.out-of-moves") {
    forAll { (ship:Ship, loc:CosmicLocation) =>
      val cmd = MoveShip(Moves(0), ship, loc)
      MoveShip.validate(cmd) shouldBe Validated.Invalid(NonEmptyList.one(NoMovesRemaining))
    }
  }
}
