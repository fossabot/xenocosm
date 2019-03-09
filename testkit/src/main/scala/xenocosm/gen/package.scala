package xenocosm

import org.scalacheck.Gen
import spire.math.UInt
import squants.motion.{CubicMetersPerSecond, MetersPerSecond, SpeedOfLight}
import squants.space._
import squants.time.Seconds
import squants.UnitOfMeasure
import galaxique.data.Point3

import xenocosm.data._

package object gen {
  lazy val foreignID:Gen[ForeignID] = Gen.alphaNumStr.map(ForeignID.apply)

  lazy val scaledPoint3:UnitOfMeasure[Length] => Gen[Point3] = uom =>
    for {
      x <- Gen.chooseNum(-1000, 1000)
      y <- Gen.chooseNum(-1000, 1000)
      z <- Gen.chooseNum(-1000, 1000)
    } yield Point3(uom(x), uom(y), uom(z))

  lazy val cosmicLocation:Gen[CosmicLocation] =
    for {
      uuid <- Gen.uuid
      locU <- scaledPoint3(KiloParsecs)
      locG <- scaledPoint3(Parsecs)
      locS <- scaledPoint3(AstronomicalUnits)
    } yield CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))

  lazy val cargo:Gen[Cargo] = Gen.const(Vacuum)

  private lazy val cargoManifest:Gen[(Cargo, Volume)] =
    for {
      cargo <- cargo
      volume <- Gen.posNum[Double].map(CubicMeters.apply[Double])
    } yield cargo -> volume

  lazy val fuelTank:Gen[FuelTank] =
    for {
      used <- Gen.posNum[Double].map(_ / 2)
      unused <- Gen.posNum[Double].map(_ / 2)
    } yield FuelTank(CubicMeters(used), CubicMeters(unused))

  lazy val engine:Gen[Engine] =
    for {
      speed <- Gen.chooseNum[Double](0, (SpeedOfLight * 0.99999).toMetersPerSecond)
      consumptionRate <- Gen.posNum[Double].map(CubicMetersPerSecond.apply[Double])
    } yield Engine(MetersPerSecond(speed), consumptionRate)

  lazy val navigation:Gen[Navigation] = Gen.posNum[Double].map(x => Navigation(Meters(x)))
  lazy val cargoHold:Gen[CargoHold] = Gen.mapOf(cargoManifest).map(CargoHold.apply)
  lazy val shipModule:Gen[ShipModule] = Gen.oneOf(Gen.const(EmptyModule), fuelTank, engine, navigation, cargoHold)
  lazy val shipModules:Gen[ShipModules] = Gen.listOf(shipModule)

  lazy val ship:Gen[Ship] =
    for {
      uuid <- Gen.uuid
      loc <- cosmicLocation
      modules <- shipModules
    } yield Ship(uuid, loc, modules)

  lazy val elapsedTime:Gen[ElapsedTime] =
    for {
      moving <- Gen.posNum[Long].map(Seconds.apply[Long])
      rest <- Gen.posNum[Long].map(Seconds.apply[Long])
    } yield ElapsedTime(moving, rest)

  lazy val trader:Gen[Trader] =
    for {
      uuid <- Gen.uuid
      ship <- ship
      elapsedTime <- elapsedTime
    } yield Trader(uuid, ship, elapsedTime)

  lazy val identity:Gen[Identity] =
    for {
      uuid <- Gen.uuid
      ref <- Gen.option(foreignID)
      moves <- Gen.posNum[Int].map(UInt.apply)
      trader <- Gen.option(trader)
    } yield Identity(uuid, ref, moves, trader)

  private lazy val errCannotNavigate:Gen[XenocosmError] =
    for {
      distance <- Gen.posNum[Double]
    } yield CannotNavigate(Meters(distance))

  private lazy val errNotEnoughFuel:Gen[XenocosmError] =
    for {
      volume <- Gen.posNum[Double]
    } yield NotEnoughFuel(CubicMeters(volume))

  lazy val error:Gen[XenocosmError] = Gen.oneOf(
    Gen.const(NoMovesRemaining),
    errCannotNavigate,
    errNotEnoughFuel
  )

  private lazy val evtTraderCreated:Gen[TraderCreated] =
    for {
      trader <- trader
    } yield TraderCreated(trader)

  private lazy val evtTraderSelected:Gen[TraderSelected] =
    for {
      trader <- trader
    } yield TraderSelected(trader)

  private lazy val evtShipMoved:Gen[ShipMoved] =
    for {
      loc <- cosmicLocation
    } yield ShipMoved(loc)

  lazy val event:Gen[XenocosmEvent] =
    Gen.oneOf(evtShipMoved, evtTraderCreated, evtTraderSelected)
}
