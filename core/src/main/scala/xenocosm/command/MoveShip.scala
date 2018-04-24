package xenocosm
package command

import cats.data.Validated

import xenocosm.data.{CosmicLocation, Moves, Ship}
import xenocosm.error.NoMovesRemaining
import xenocosm.event.ShipMoved

final case class MoveShip(moves:Moves, ship:Ship, loc:CosmicLocation)

object MoveShip {

  def validate(command:MoveShip):ValidatedCommand[ShipMoved] =
    command.moves match {
      case Moves(0) => Validated.invalidNel(NoMovesRemaining)
      case moves => Validated.valid(ShipMoved(moves - 1, command.ship.copy(loc = command.loc)))
    }
}
