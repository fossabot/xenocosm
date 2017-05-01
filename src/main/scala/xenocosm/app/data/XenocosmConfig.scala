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
import squants.thermal.Temperature

import xenocosm.interop.instances._

final case class XenocosmConfig(
  http:XenocosmConfig.HttpConfig,
  services:XenocosmConfig.ServiceConfig,
  universe:XenocosmConfig.UniverseConfig,
  galaxy:XenocosmConfig.GalaxyConfig,
  morganKeenan:XenocosmConfig.MorganKeenanConfig,
  stellarSystemBody:XenocosmConfig.StellarSystemBodyConfig
)

object XenocosmConfig {

  final case class AgeRange(min:Long, max:Long, epsilon:Long) {
    val interval:Bounded[Long] = Bounded(min, max, 0)
    val dist:Dist[Long] = interval.dist(min, max, epsilon)
  }

  final case class LengthRange(min:Length, max:Length, epsilon:Length) {
    val interval:Bounded[Length] = Bounded(min, max, 0)
    val dist:Dist[Length] = interval.dist(min, max, epsilon)
  }

  final case class PowerRange(min:Power, max:Power, epsilon:Power) {
    val interval:Bounded[Power] = Bounded(min, max, 0)
    val dist:Dist[Power] = interval.dist(min, max, epsilon)
  }

  final case class MassRange(min:Mass, max:Mass, epsilon:Mass) {
    val interval:Bounded[Mass] = Bounded(min, max, 0)
    val dist:Dist[Mass] = interval.dist(min, max, epsilon)
  }

  final case class TemperatureRange(min:Temperature, max:Temperature, epsilon:Temperature) {
    val interval:Bounded[Temperature] = Bounded(min, max, 0)
    val dist:Dist[Temperature] = interval.dist(min, max, epsilon)
  }

  final case class MorganKeenanConfig(
    frequencies:Map[String,Double],
    temperatures:Map[String, TemperatureRange],
    luminosities:Map[String, PowerRange],
    radii:Map[String, LengthRange],
    masses:Map[String, MassRange]
  )

  final case class StellarSystemBodyConfig(
    semiMajorAxis:LengthRange,
    planetRadius:LengthRange,
    planetMass:MassRange
  )

  final case class GalaxyConfig(diameter:LengthRange, luminosity:PowerRange, mass:MassRange)
  final case class UniverseConfig(age:AgeRange, diameter:LengthRange)
  final case class HttpConfig(host:String, port:Int)

  final case class IntergalacticCoordinateServiceConfig(scale:Length)
  final case class InterstellarCoordinateServiceConfig(scale:Length)
  final case class InterplanetaryCoordinateServiceConfig(scale:Length)

  final case class ServiceConfig(
    intergalacticCoordinates:IntergalacticCoordinateServiceConfig,
    interstellarCoordinates:InterstellarCoordinateServiceConfig,
    interplanetaryCoordinates:InterplanetaryCoordinateServiceConfig
  )

  def load:Either[ConfigReaderFailures, XenocosmConfig] = loadConfig[XenocosmConfig]
}
