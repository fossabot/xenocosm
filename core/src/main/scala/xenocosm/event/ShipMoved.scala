package xenocosm
package event

import xenocosm.data.{Moves, Ship}

final case class ShipMoved(moves:Moves, ship:Ship)
