package xenocosm

import xenocosm.data.{CosmicLocation, Trader}

sealed trait XenocosmEvent
final case class ShipMoved(loc:CosmicLocation) extends XenocosmEvent
final case class TraderCreated(trader:Trader) extends XenocosmEvent
final case class TraderSelected(trader:Trader) extends XenocosmEvent
case object TraderUnselected extends XenocosmEvent
