package xenocosm
package interop

import cats.Order
import cats.instances.double._
import spire.algebra.{AdditiveGroup, IsReal}
import spire.math.{NumberTag, Real}
import spire.random.{Dist, Uniform}
import squants.energy.{Power, Watts}
import squants.mass.{Grams, Mass}
import squants.space.{Length, Meters}
import squants.thermal.{Kelvin, Temperature}

object SquantsInstances {

  trait LengthInstances {

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

  trait MassInstances {

    implicit val squantsMassHasOrder: Order[Mass] = Order.by(_.toGrams)
    implicit val squantsMassHasDist: Dist[Mass] = Dist.long.map(Grams.apply[Long])

    implicit val squantsMassHasAdditiveGroup: AdditiveGroup[Mass] =
      new AdditiveGroup[Mass] {
        def negate(x: Mass): Mass = x.negate
        def zero: Mass = Grams(0)
        def plus(x: Mass, y: Mass): Mass = x + y
      }

    implicit val squantsMassHasUniform: Uniform[Mass] =
      new Uniform[Mass] {
        def apply(min:Mass, max:Mass):Dist[Mass] =
          Dist.
            gen(_.nextDouble(min.toGrams, max.toGrams)).
            map(Grams.apply[Double])
      }
  }

  trait PowerInstances {

    implicit val squantsPowerHasOrder: Order[Power] = Order.by(_.toWatts)
    implicit val squantsPowerHasDist: Dist[Power] = Dist.long.map(Watts.apply[Long])

    implicit val squantsPowerHasAdditiveGroup: AdditiveGroup[Power] =
      new AdditiveGroup[Power] {
        def negate(x: Power): Power = x.negate
        def zero: Power = Watts(0)
        def plus(x: Power, y: Power): Power = x + y
      }

    implicit val squantsPowerHasUniform: Uniform[Power] =
      new Uniform[Power] {
        def apply(min:Power, max:Power):Dist[Power] =
          Dist.
            gen(_.nextDouble(min.toWatts, max.toWatts)).
            map(Watts.apply[Double])
      }
  }

  trait TemperatureInstances {

    implicit val squantsTemperatureHasOrder: Order[Temperature] = Order.by(_.toKelvinDegrees)
    implicit val squantsTemperatureHasDist: Dist[Temperature] = Dist.long.map(Kelvin.apply[Long])

    implicit val squantsTemperatureHasAdditiveGroup: AdditiveGroup[Temperature] =
      new AdditiveGroup[Temperature] {
        def negate(x: Temperature): Temperature = x.negate
        def zero: Temperature = Kelvin(0)
        def plus(x: Temperature, y: Temperature): Temperature = x + y
      }

    implicit val squantsTemperatureHasUniform: Uniform[Temperature] =
      new Uniform[Temperature] {
        def apply(min:Temperature, max:Temperature):Dist[Temperature] =
          Dist.
            gen(_.nextDouble(min.toKelvinDegrees, max.toKelvinDegrees)).
            map(Kelvin.apply[Double])
      }
  }

  object instances extends LengthInstances with MassInstances with PowerInstances with TemperatureInstances
}
