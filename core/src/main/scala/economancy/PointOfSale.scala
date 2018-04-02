package economancy

import java.time.Instant
import squants.market.Money

/**
  * A point of sale for commodity `A` in terms of `Money`
  * @tparam A The commodity
  */
trait PointOfSale[A] {
  def unitValue(commodity:A, at:Instant):Money
  def unitMarkup(commodity:A, at:Instant):BigDecimal

  def price(commodity:A, at:Instant):Money = unitValue(commodity, at) * unitMarkup(commodity, at)
}

object PointOfSale {
  def apply[A](implicit $ev:PointOfSale[A]):PointOfSale[A] = $ev

  def const[A](value:Money, percentage:BigDecimal):PointOfSale[A] =
    new PointOfSale[A] {
      def unitValue(commodity:A, at:Instant):Money = value
      def unitMarkup(commodity:A, at:Instant):BigDecimal = percentage
    }
  def const[A](value:Money):PointOfSale[A] = const(value, BigDecimal(1))

  trait Syntax {
    implicit class PointOfSaleOps[A](underlying:A)(implicit val $ev:PointOfSale[A]) {
      def price(now:Instant):Money = $ev.price(underlying, now)
    }
  }
  object syntax extends Syntax
}
