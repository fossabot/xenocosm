package economancy

trait Currency[A] {
  def symbol(a:A):String
}

object Currency {
  def apply[A](implicit $ev:Currency[A]):Currency[A] = $ev

  trait Syntax {
    implicit class CurrencyOps[A](underlying:A)(implicit $ev:Currency[A]) {
      def symbol:String = $ev.symbol(underlying)
    }
  }
  object syntax extends Syntax
}
