package xenocosm
package event

import xenocosm.data.{Moves, Trader}

final case class TraderCreated(moves:Moves, trader:Trader) extends XenocosmEvent
