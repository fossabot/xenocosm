package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import cavernetics.FSM
import monocle.Lens
import monocle.macros.GenLens
import spire.random.Dist

final case class Trader(uuid:UUID, ship:Ship, elapsed:ElapsedTime)

object Trader {
  import cats.implicits._
  import interop.java.instances._
  import Ship.instances._
  import ElapsedTime.instances._
  import FSM.syntax._

  val shipPL:Lens[Trader, Ship] = GenLens[Trader](_.ship)
  val elapsedPL:Lens[Trader, ElapsedTime] = GenLens[Trader](_.elapsed)

  val travelTime:Trader => CosmicLocation => ElapsedTime = trader => loc =>
    Ship.fuelNeeded(trader.ship)(loc)
      .flatMap(v => trader.ship.bestEngine.map(ShipModule.travelTime(_)(v)))
      .getOrElse(ElapsedTime.zero)

  trait Instances {
    implicit val traderHasEq:Eq[Trader] = Eq.fromUniversalEquals[Trader]

    implicit val traderHasDist:Dist[Trader] =
      for {
        uuid <- Dist[UUID]
        ship <- Dist[Ship]
      } yield Trader(uuid, ship, ElapsedTime.zero)

    implicit val traderHasFSM:FSM[Trader, XenocosmEvent, XenocosmError] = FSM {
      case (trader, event @ ShipMoved(loc)) =>
        println(travelTime(trader)(loc))
        val elapsed = trader.elapsed |+| travelTime(trader)(loc)
        trader.ship
          .transition(event)
          .map(ship => shipPL.set(ship) andThen elapsedPL.set(elapsed))
          .map(_ apply trader)
    }
  }
  object instances extends Instances
}
