package xenocosm
package command

import cats.data.{NonEmptyList, Validated}
import spire.random.Generator
import spire.random.rng.Serial

import xenocosm.data.Moves
import xenocosm.error.NoMovesRemaining

class CreateTraderSpec extends xenocosm.test.XenocosmSuite {
  implicit val gen:Generator = Serial(0L)

  test("CreateTrader.valid") {
    val cmd = CreateTrader(Moves(1))
    CreateTrader.validate(cmd).isValid shouldBe true
  }

  test("CreateTrader.invalid.out-of-moves") {
    val cmd = CreateTrader(Moves(0))
    CreateTrader.validate(cmd) shouldBe Validated.Invalid(NonEmptyList.one(NoMovesRemaining))
  }
}
