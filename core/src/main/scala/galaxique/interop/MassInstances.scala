package galaxique
package interop

import cats.Order
import spire.algebra.AdditiveGroup
import spire.random.{Dist, Uniform}
import squants.mass.{Grams, Mass}

trait MassInstances {
  import cats.implicits._

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
