package interop.squants.energy

import cats.Order
import spire.algebra.AdditiveGroup
import spire.random.{Dist, Uniform}
import squants.energy.{Power, Watts}

trait PowerInstances {
  import cats.implicits._

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
