package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import cavernetics.FSM
import monocle.{Lens, Optional}
import monocle.macros.GenLens
import spire.math.UInt

final case class Identity(uuid:UUID, ref:Option[ForeignID], moves:UInt, trader:Option[Trader])

object Identity {
  val startingMoves:UInt = UInt(100)

  def apply(ref:ForeignID):Identity =
    Identity(UUID.randomUUID(), Some(ref), startingMoves, None)

  val movesPL:Lens[Identity, UInt] =
    GenLens[Identity](_.moves)

  val traderPO:Optional[Identity, Trader] =
    Optional[Identity, Trader](_.trader)(trader => _.copy(trader = Some(trader)))

  val shipPO:Optional[Identity, Ship] =
    traderPO.composeLens(Trader.shipPL)

  def elapse(identity:Identity, elapsedTime:ElapsedTime):Identity =
    identity.trader match {
      case Some(trader) => traderPO.set(Trader.elapse(trader, elapsedTime))(identity)
      case None => identity
    }

  trait Instances {
    implicit val identityHasEq:Eq[Identity] = Eq.fromUniversalEquals[Identity]
    implicit val identityHasFSM:FSM[Identity, XenocosmEvent] = FSM {
      case (identity, ShipMoved(moves, ship, elapsed)) =>
        (movesPL.set(moves) andThen shipPO.set(ship))(elapse(identity, elapsed))

      case (identity, TraderCreated(moves, trader)) =>
        (movesPL.set(moves) andThen traderPO.set(trader))(identity)

      case (identity, TraderSelected(moves, trader)) =>
        (movesPL.set(moves) andThen traderPO.set(trader))(identity)
    }
  }
  object instances extends Instances
}
