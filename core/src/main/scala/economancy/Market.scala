package economancy

import java.time.Instant
import squants.market.Money

/**
  * A market for commodity `A` in terms of `Money`
  * @tparam A The commodity
  */
trait Market[A] extends PointOfSale[A] {
  def materialsCost:Money
  def supply(commodity:A, at:Instant):BigDecimal
  def demand(commodity:A, at:Instant):BigDecimal

  def unitValue(commodity:A, at:Instant):Money = materialsCost
  def unitMarkup(commodity:A, at:Instant): BigDecimal =
    (demand(commodity, at) / supply(commodity, at)) + BigDecimal(1)
}

object Market {
  def apply[A](implicit $ev:Market[A]):Market[A] = $ev

  trait Syntax {
    implicit class MarketOps[A](underlying:A)(implicit $ev:Market[A]) {
      def supply(now:Instant):BigDecimal = $ev.supply(underlying, now)
      def demand(now:Instant):BigDecimal = $ev.demand(underlying, now)
    }
  }
  object syntax extends Syntax
}
