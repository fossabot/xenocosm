package economancy

trait ExchangeRate[A, B] {
  def rate(a:Currency[A], b:Currency[B]):BigDecimal
}

object ExchangeRate {
  def apply[A, B](implicit $ev: ExchangeRate[A, B]):ExchangeRate[A, B] = $ev
  def apply[A, B](f:Currency[A] => Currency[B] => BigDecimal):ExchangeRate[A, B] =
    new ExchangeRate[A, B] {
      def rate(lhs:Currency[A], rhs:Currency[B]):BigDecimal = f(lhs)(rhs)
    }

  def identity[A]:ExchangeRate[A, A] =
    new ExchangeRate[A, A] {
      def rate(lhs:Currency[A], rhs:Currency[A]):BigDecimal = BigDecimal(1)
    }

  trait Syntax {
    implicit class ExchangeRateOps[A, B](underlying:Currency[A])(implicit $ev:ExchangeRate[A, B]) {
      def rate(currency:Currency[B]):BigDecimal = $ev.rate(underlying, currency)
    }
  }
  object syntax extends Syntax
}
