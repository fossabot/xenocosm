package economancy

import java.time.Instant
import spire.syntax.vectorSpace._

import economancy.data.Money
import Money.instances._

/**
  * A market for commodity `A` in terms of `Money[B]`
  * @tparam A
  * @tparam B
  */
trait Market[A, B] extends PointOfSale[A, B] {
  def materialsCost:Money[B]
  def supply(commodity:A, at:Instant):BigDecimal
  def demand(commodity:A, at:Instant):BigDecimal

  def unitValue(commodity:A, at:Instant):Money[B] =
    materialsCost :* (supply(commodity, at) / demand(commodity, at))
}

object Market {
  def apply[A, B](implicit $ev:Market[A, B]):Market[A, B] = $ev

  trait Syntax {
    implicit class MarketOps[A, B](underlying:A)(implicit $ev:Market[A, B]) {
      def supply(now:Instant):BigDecimal = $ev.supply(underlying, now)
      def demand(now:Instant):BigDecimal = $ev.demand(underlying, now)
    }
  }
  object syntax extends Syntax
}
