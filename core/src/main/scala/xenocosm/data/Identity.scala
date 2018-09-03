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
  import Trader.instances._
  import FSM.syntax._

  val startingMoves:UInt = UInt(1000)

  def apply(ref:ForeignID):Identity =
    Identity(UUID.randomUUID(), Some(ref), startingMoves, None)

  val movesPL:Lens[Identity, UInt] =
    GenLens[Identity](_.moves)

  val traderPL:Lens[Identity, Option[Trader]] =
    GenLens[Identity](_.trader)

  val traderPO:Optional[Identity, Trader] =
    Optional[Identity, Trader](_.trader)(trader => _.copy(trader = Some(trader)))

  trait Instances {
    implicit val identityHasEq:Eq[Identity] = Eq.fromUniversalEquals[Identity]

    implicit val identityHasFSM:FSM[Identity, XenocosmEvent, XenocosmError] = FSM {
      case (identity, TraderCreated(trader)) =>
        Right(traderPO.set(trader)(identity))

      case (identity, TraderSelected(trader)) =>
        Right(traderPO.set(trader)(identity))

      case (identity, TraderDeselected(traderID)) if identity.trader.exists(_.uuid == traderID) =>
        Right(traderPL.set(None)(identity))

      case (identity, TraderDeselected(_)) =>
        Right(identity)

      case (identity, _) if identity.moves <= UInt(0) =>
        Left(NoMovesRemaining)

      case (identity, event) =>
        identity.trader match {
          case None => Left(NoTraderSelected)
          case Some(trader) =>
            trader
              .transition(event)
              .map(traderPO.set(_) andThen movesPL.set(identity.moves - UInt(1)))
              .map(_ apply identity)
        }
    }
  }
  object instances extends Instances
}
