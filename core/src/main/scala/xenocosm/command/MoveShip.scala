package xenocosm
package command

import cats.data.{NonEmptyList, Validated}

import xenocosm.data.{CosmicLocation, Moves, Ship}
import xenocosm.error.{NoMovesRemaining, XenocosmError}
import xenocosm.event.ShipMoved

final case class MoveShip(moves:Moves, ship:Ship, loc:CosmicLocation)

object MoveShip {

  def validate(command:MoveShip):Validated[NonEmptyList[XenocosmError], XenocosmEvent] =
    command.moves match {
      case Moves(0) => Validated.invalidNel(NoMovesRemaining)
      case moves => Validated.valid(ShipMoved(moves - 1, command.ship.copy(loc = command.loc)))
    }
}
