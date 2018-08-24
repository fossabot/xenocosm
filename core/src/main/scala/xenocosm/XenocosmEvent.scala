package xenocosm

import spire.math.UInt
import squants.time.Time

import xenocosm.data.{Ship, Trader}

sealed trait XenocosmEvent
final case class ShipMoved(moves:UInt, ship:Ship, moving:Time, stationary:Time) extends XenocosmEvent
final case class TraderCreated(moves:UInt, trader:Trader) extends XenocosmEvent
