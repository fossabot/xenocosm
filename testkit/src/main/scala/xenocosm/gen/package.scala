package xenocosm

import org.scalacheck.Gen
import spire.math.UInt
import squants.motion.{CubicMetersPerSecond, MetersPerSecond}
import squants.space.{CubicMeters, Meters, Volume}
import squants.time.Seconds

import xenocosm.data._

package object gen {
  lazy val foreignID:Gen[ForeignID] = Gen.alphaNumStr.map(ForeignID.apply)

  lazy val cosmicLocation:Gen[CosmicLocation] =
    for {
      uuid <- Gen.uuid
      locU <- galaxique.gen.point3
      locG <- galaxique.gen.point3
      locS <- galaxique.gen.point3
    } yield CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))

  lazy val identity:Gen[Identity] =
    for {
      uuid <- Gen.uuid
      ref <- Gen.option(foreignID)
      moves <- Gen.posNum[Int].map(UInt.apply)
    } yield Identity(uuid, ref, moves)

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
      speed <- Gen.posNum[Double].map(MetersPerSecond.apply[Double])
      consumptionRate <- Gen.posNum[Double].map(CubicMetersPerSecond.apply[Double])
    } yield Engine(speed, consumptionRate)

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

  lazy val trader:Gen[Trader] =
    for {
      uuid <- Gen.uuid
      ship <- ship
    } yield Trader(uuid, ship)

  private lazy val errTooFar:Gen[XenocosmError] =
    for {
      distance <- Gen.posNum[Double]
    } yield TooFar(Meters(distance))

  lazy val error:Gen[XenocosmError] = Gen.oneOf(Gen.const(NoMovesRemaining), errTooFar)

  private lazy val evtTraderCreated:Gen[TraderCreated] =
    for {
      moves <- Gen.posNum[Int].map(UInt.apply)
      trader <- trader
    } yield TraderCreated(moves, trader)

  private lazy val evtShipMoved:Gen[ShipMoved] =
    for {
      moves <- Gen.posNum[Int].map(UInt.apply)
      ship <- ship
      moving <- Gen.posNum[Double].map(Seconds.apply[Double])
      stationary <- Gen.posNum[Double].map(Seconds.apply[Double])
    } yield ShipMoved(moves, ship, moving, stationary)

  lazy val event:Gen[XenocosmEvent] =
    Gen.oneOf(evtShipMoved, evtTraderCreated)
}
