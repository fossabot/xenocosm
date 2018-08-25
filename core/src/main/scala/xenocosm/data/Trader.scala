package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import monocle.Lens
import monocle.macros.GenLens
import spire.random.Dist

final case class Trader(uuid:UUID, ship:Ship, elapsed:ElapsedTime)

object Trader {
  import cats.implicits._
  import interop.java.instances._
  import Ship.instances._
  import ElapsedTime.instances._

  val shipPL:Lens[Trader, Ship] = GenLens[Trader](_.ship)
  def elapse(trader:Trader, elapsedTime:ElapsedTime):Trader =
    trader.copy(elapsed = trader.elapsed |+| elapsedTime)

  trait Instances {
    implicit val traderHasEq:Eq[Trader] = Eq.fromUniversalEquals[Trader]
    implicit val traderHasDist:Dist[Trader] =
      for {
        uuid <- Dist[UUID]
        ship <- Dist[Ship]
      } yield Trader(uuid, ship, ElapsedTime.zero)
  }
  object instances extends Instances
}
