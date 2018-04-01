package economancy
package data

import cats.implicits._
import spire.algebra.{Field, VectorSpace}
import spire.std.bigDecimal._

final case class Money[A](items:Map[Currency[A], BigDecimal])

object Money {
  def empty[A]:Money[A] = Money(Map.empty[Currency[A], BigDecimal])
  def apply[A](currency:Currency[A], amount:BigDecimal):Money[A] =
    Money(Map(currency -> amount))

  trait Instances {
    implicit def moneyHasBigDecimalVectorSpace[A]:VectorSpace[Money[A], BigDecimal] =
      new VectorSpace[Money[A], BigDecimal] {
        implicit def scalar: Field[BigDecimal] = Field.apply[BigDecimal]
        def zero:Money[A] = empty
        def plus(x:Money[A], y:Money[A]): Money[A] = Money(x.items |+| y.items)
        def negate(x:Money[A]):Money[A] = Money(x.items.mapValues(-_))
        def timesl(r:BigDecimal, v:Money[A]):Money[A] = Money(v.items.mapValues(_ * r))
      }
  }
  object instances extends Instances
}
