package interop.squants.space

import cats.Order
import spire.algebra.{AdditiveGroup, IsReal}
import spire.math.{NumberTag, Real}
import spire.random.{Dist, Uniform}
import squants.space.{Length, Meters}

trait LengthInstances {
  import cats.implicits._

  implicit val squantsLengthHasOrder: Order[Length] = Order.by(_.toMeters)
  implicit val squantsLengthHasDist: Dist[Length] = Dist.long.map(Meters.apply[Long])

  implicit val squantsLengthHasAdditiveGroup: AdditiveGroup[Length] =
    new AdditiveGroup[Length] {
      def negate(x: Length): Length = x.negate
      def zero: Length = Meters(0)
      def plus(x: Length, y: Length): Length = x + y
    }

  implicit val squantsLengthHasUniform: Uniform[Length] =
    new Uniform[Length] {
      def apply(min:Length, max:Length):Dist[Length] =
        Dist.
          gen(_.nextDouble(min.toMeters, max.toMeters)).
          map(Meters.apply[Double])
    }

  implicit val squantsLengthHasNumberTag: NumberTag[Length] =
    new NumberTag[Length] {
      def isSigned: Boolean = true
      def hasNaN: Option[Length] = Some(Meters(Double.NaN))
      def hasMaxValue: Option[Length] = Some(Meters(Double.MaxValue))
      def isNaN(a: Length): Boolean = a.toMeters == Double.NaN
      def hasPositiveInfinity: Option[Length] = None
      def resolution: NumberTag.Resolution = NumberTag.Approximate
      def hasZero: Option[Length] = Some(Meters(0))
      def hasNegativeInfinity: Option[Length] = None
      def isInfinite(a: Length): Boolean = false
      def overflows: Boolean = true
      def hasMinValue: Option[Length] = Some(Meters(Double.MinValue))
    }

  implicit val squantsLengthIsReal: IsReal[Length] =
    new IsReal[Length] {
      def ceil(a: Length): Length = a.ceil
      def floor(a: Length): Length = a.floor
      def round(a: Length): Length = a.rint
      def isWhole(a: Length): Boolean = a.rint == a
      def toDouble(a: Length): Double = a.toMeters
      def toReal(a: Length): Real = Real(a.toMeters)
      def compare(x: Length, y: Length): Int = x.compare(y)
      def signum(a: Length): Int = a.compare(Meters(0))
      def abs(a: Length): Length = a.abs
    }
}
