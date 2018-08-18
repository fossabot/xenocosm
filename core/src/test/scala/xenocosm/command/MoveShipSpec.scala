package xenocosm
package command

import java.util.UUID
import galaxique.data.{Galaxy, Point3}
import spire.math.UInt
import spire.random.{Generator, Random, Seed}
import squants.motion.{CubicMetersPerSecond, SpeedOfLight}
import squants.space.{AstronomicalUnits, CubicMeters, Meters, Parsecs}
import squants.time.Seconds

import xenocosm.data._

class MoveShipSpec extends xenocosm.test.XenocosmSuite {
  import CommandHandler.syntax._
  import XenocosmCommand.instances._

  val seed:Seed = Seed.zero
  val ship:Ship = Ship(
    UUID.fromString("00000000-0000-0000-0000-000000000000"),
    CosmicLocation(
      UUID.fromString("00000000-0000-0000-0000-000000000000"),
      Some(Point3(Parsecs(-5000000000.0), Parsecs(-5000000000.0), Parsecs(-5000000000.0))),
      Some(Point3(Parsecs(-5000.0), Parsecs(-5000.0), Parsecs(-5000.0))),
      Some(Point3(AstronomicalUnits(0), AstronomicalUnits(0), AstronomicalUnits(0)))
    ),
    ShipModules.startingLoad
  )
  val to:CosmicLocation = ship.loc.copy(locS = Some(Point3(AstronomicalUnits(1), AstronomicalUnits(1), AstronomicalUnits(1))))

  test("CommandHandler[MoveShip].verification.success") {
    val gen:Generator = Random.generatorFromSeed(seed)
    val cmd = MoveShip(UInt(1), ship, to)

    cmd.verify.value(gen) shouldBe Right(ShipMoved(
      UInt(0),
      Ship(
        UUID.fromString("00000000-0000-0000-0000-000000000000"),
        CosmicLocation(
          UUID.fromString("00000000-0000-0000-0000-000000000000"),
          Some(Point3(Parsecs(-5000000000.0), Parsecs(-5000000000.0), Parsecs(-5000000000.0))),
          Some(Point3(Parsecs(-5000.0), Parsecs(-5000.0), Parsecs(-5000.0))),
          Some(Point3(AstronomicalUnits(1), AstronomicalUnits(1), AstronomicalUnits(1)))
        ),
        List(
          EmptyModule,
          FuelTank(CubicMeters(86.43016388241479), CubicMeters(13.569836117585211)),
          ShipModule.emptyCargo(CubicMeters(100)),
          Navigation(Galaxy.scale),
          Engine(SpeedOfLight * 0.01, CubicMetersPerSecond(0.001))
        )
      ),
      Seconds(86430.16388241478),
      Seconds(86434.48571474903)
    ))
  }

  test("CommandHandler[MoveShip].verification.failure.no-moves-remaining") {
    val gen:Generator = Random.generatorFromSeed(seed)
    val cmd = MoveShip(UInt(0), ship, to)

    cmd.verify.value(gen) shouldBe Left(NoMovesRemaining)
  }

  test("CommandHandler[MoveShip].verification.failure.too-far") {
    val gen:Generator = Random.generatorFromSeed(seed)
    val to2:CosmicLocation = ship.loc.copy(locS = Some(Point3(AstronomicalUnits(2), AstronomicalUnits(2), AstronomicalUnits(2))))
    val cmd = MoveShip(UInt(1), ship, to2)

    cmd.verify.value(gen) shouldBe Left(TooFar(Meters(5.18222225513039E11)))
  }
}
