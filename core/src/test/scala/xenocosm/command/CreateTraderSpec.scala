package xenocosm
package command

import java.util.UUID
import galaxique.data.Point3
import spire.math.UInt
import spire.random.{Generator, Random, Seed}
import squants.space.{AstronomicalUnits, Parsecs}

import xenocosm.data._

class CreateTraderSpec extends xenocosm.test.XenocosmSuite {
  import CommandHandler.syntax._
  import XenocosmCommand.instances._

  val seed:Seed = Seed.zero

  test("CommandHandler[CreateTrader].verification.success") {
    val gen:Generator = Random.generatorFromSeed(seed)
    val cmd = CreateTrader(UInt(1))

    cmd.verify.value(gen) shouldBe Right(TraderCreated(
      UInt(0),
      Trader(
        UUID.fromString("00000000-0000-0000-0000-000000000000"),
        Ship(
          UUID.fromString("00000000-0000-0000-0000-000000000000"),
          CosmicLocation(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Some(Point3(Parsecs(-5000000000.0), Parsecs(-5000000000.0), Parsecs(-5000000000.0))),
            Some(Point3(Parsecs(-5000.0), Parsecs(-5000.0), Parsecs(-5000.0))),
            Some(Point3(AstronomicalUnits(0), AstronomicalUnits(0), AstronomicalUnits(0)))
          ),
          ShipModules.startingLoad
        )
      )
    ))
  }

  test("CommandHandler[CreateTrader].verification.failure.no-moves-remaining") {
    val gen:Generator = Random.generatorFromSeed(seed)
    val cmd = CreateTrader(UInt(0))

    cmd.verify.value(gen) shouldBe Left(NoMovesRemaining)
  }
}
