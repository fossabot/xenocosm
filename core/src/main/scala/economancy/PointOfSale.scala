package economancy

import java.time.Instant
import economancy.data.Money

/**
  * A point of sale for commodity `A` in terms of `Money[B]`
  * @tparam A
  * @tparam B
  */
trait PointOfSale[A, B] {
  def unitValue(commodity:A, at:Instant):Money[B]
}

object PointOfSale {
  def apply[A, B](implicit $ev:PointOfSale[A, B]):PointOfSale[A, B] = $ev

  def const[A, B](value:Money[B]):PointOfSale[A, B] =
    new PointOfSale[A, B] {
      def unitValue(commodity:A, at:Instant):Money[B] = value
    }

  trait Syntax {
    implicit class PointOfSaleOps[A, B](underlying:A)(implicit val $ev:PointOfSale[A, B]) {
      def price(now:Instant):Money[B] = $ev.unitValue(underlying, now)
    }
  }
  object syntax extends Syntax
}
