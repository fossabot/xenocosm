package economancy

import economancy.data.Money
import spire.implicits._

trait TimeValue[F[_]] {
  def rate[A]:F[A] => BigDecimal
  def presentValue[A]:F[A] => Money[A]
  def updatedValue[A]:(F[A], Money[A]) => F[A]

  def futureValue[A](F:F[A], periods:Int):F[A] =
    updatedValue[A](F, TimeValue.futureValue(rate(F), presentValue(F), periods))

}

object TimeValue {
  import Money.instances._

  private def futureValue[A](rate:BigDecimal, presentValue:Money[A], periods:Int):Money[A] =
    presentValue :* (1 + rate).pow(periods)

  trait Syntax {
    implicit class TimeValueOps[F[_], T](underlying:F[T])(implicit val $tv:TimeValue[F]) {
      def future(periods:Int):F[T] = $tv.futureValue(underlying, periods)
    }
  }
}
