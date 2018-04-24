package xenocosm
package command

import cats.data.Validated
import spire.random.{Dist, Generator}

import xenocosm.data.{Moves, Trader}
import xenocosm.error.NoMovesRemaining
import xenocosm.event.TraderCreated

final case class CreateTrader(moves:Moves)

object CreateTrader {
  import Trader.instances._

  def validate(command:CreateTrader)(implicit gen:Generator):ValidatedCommand[TraderCreated] =
    command.moves match {
      case Moves(0) => Validated.invalidNel(NoMovesRemaining)
      case moves => Validated.valid(TraderCreated(moves - 1, Dist[Trader].apply(gen)))
    }
}
