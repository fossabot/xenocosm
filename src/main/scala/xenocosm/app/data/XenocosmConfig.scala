package xenocosm
package app
package data

import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.module.squants._
import spire.math.Bounded
import spire.random.Dist
import spire.std.long._
import squants.energy._
import squants.mass._
import squants.space._

import instances.interop._

final case class XenocosmConfig(
  universe:XenocosmConfig.Universe,
  galaxy:XenocosmConfig.Galaxy
)

object XenocosmConfig {

  final case class AgeRange(min:Long, max:Long) {
    def interval:Bounded[Long] = Bounded(min, max, 0)
    def dist(epsilon:Long):Dist[Long] = interval.dist(min, max, epsilon)
  }

  final case class LengthRange(min:Length, max:Length) {
    def interval:Bounded[Length] = Bounded(min, max, 0)
    def dist(epsilon:Length):Dist[Length] = interval.dist(min, max, epsilon)
  }

  final case class PowerRange(min:Power, max:Power) {
    def interval:Bounded[Power] = Bounded(min, max, 0)
    def dist(epsilon:Power):Dist[Power] = interval.dist(min, max, epsilon)
  }

  final case class MassRange(min:Mass, max:Mass) {
    def interval:Bounded[Mass] = Bounded(min, max, 0)
    def dist(epsilon:Mass):Dist[Mass] = interval.dist(min, max, epsilon)
  }

  final case class Galaxy(diameter:LengthRange, luminosity:PowerRange, mass:MassRange)
  final case class Universe(age:AgeRange, diameter:LengthRange)

  def load:Either[ConfigReaderFailures, XenocosmConfig] = loadConfig[XenocosmConfig]
}
