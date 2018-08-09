package interop.squants.thermal

import cats.Order
import spire.algebra.AdditiveGroup
import spire.random.{Dist, Uniform}
import squants.thermal.{Kelvin, Temperature}

trait TemperatureInstances {
  import cats.implicits._

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
