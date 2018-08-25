package xenocosm

import spire.math.UInt

import xenocosm.data.{ElapsedTime, Ship, Trader}

sealed trait XenocosmEvent
final case class ShipMoved(moves:UInt, ship:Ship, elapsed:ElapsedTime) extends XenocosmEvent
final case class TraderCreated(moves:UInt, trader:Trader) extends XenocosmEvent
final case class TraderSelected(moves:UInt, trader:Trader) extends XenocosmEvent
