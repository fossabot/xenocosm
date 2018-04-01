package galaxique

import spire.math.Interval
import spire.math.interval._
import spire.random.Dist
import squants.energy.{Power, SolarLuminosities}
import squants.mass.{Mass, SolarMasses}
import squants.space.{Length, SolarRadii}
import squants.thermal.{Kelvin, Temperature}

/** Morgan-Keenan Stellar Classification
  *
  * http://www.handprint.com/ASTRO/specclass.html
  */
final case class MorganKeenan(
  classification:Char,
  temperature:Interval[Temperature],
  luminosity:Interval[Power],
  radius:Interval[Length],
  mass:Interval[Mass]) extends Product with Serializable

object MorganKeenan {
  import interop.length._
  import interop.mass._
  import interop.power._
  import interop.temperature._

  def fromClassification(classification:Char):Option[MorganKeenan] =
    for {
      temperature <- temperatures.get(classification)
      luminosity <- luminosities.get(classification)
      radius <- radii.get(classification)
      mass <- masses.get(classification)
    } yield MorganKeenan(classification, temperature, luminosity, radius, mass)

  def fromClassificationEither(classification:Char):Either[String, MorganKeenan] =
    fromClassification(classification) match {
      case Some(mk) => Right(mk)
      case None => Left(s"Unsupported Morgan-Keenan classification '${classification.toString}'.")
    }

  // Morgan-Keenan classification frequencies
  // scalastyle:off magic.number
  lazy private val frequencies:Map[Char, BigDecimal] = Map(
    'K' -> BigDecimal(0.276),
    'F' -> BigDecimal(0.22),
    'G' -> BigDecimal(0.196),
    'A' -> BigDecimal(0.16),
    'B' -> BigDecimal(0.089),
    'M' -> BigDecimal(0.05),
    'O' -> BigDecimal(0.0023),
    'S' -> BigDecimal(0.0014),
    'C' -> BigDecimal(0.000001),
    'L' -> BigDecimal(0.0000001),
    'T' -> BigDecimal(0.00000001)
  )
  // scalastyle:on magic.number

  // Temperature ranges for the Morgan-Keenan classification.
  // http://www.handprint.com/ASTRO/specclass.html
  // scalastyle:off magic.number
  lazy private val temperatures:Map[Char, Interval[Temperature]] = Map(
    'O' -> Interval.above(Kelvin(30000)),
    'B' -> Interval(Kelvin(10000), Kelvin(30000)),
    'A' -> Interval(Kelvin(7300), Kelvin(10000)),
    'F' -> Interval(Kelvin(6000), Kelvin(7300)),
    'G' -> Interval(Kelvin(5300), Kelvin(6000)),
    'K' -> Interval(Kelvin(3800), Kelvin(5300)),
    'M' -> Interval(Kelvin(2500), Kelvin(3800)),
    'C' -> Interval(Kelvin(2400), Kelvin(3200)),
    'S' -> Interval(Kelvin(2400), Kelvin(3500)),
    'L' -> Interval(Kelvin(1300), Kelvin(2100)),
    'T' -> Interval(Kelvin(600), Kelvin(1300))
  )
  // scalastyle:on magic.number

  def distTemperature(interval:Interval[Temperature]):Dist[Temperature] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => Kelvin(0)
      case Open(a:Temperature) => a
      case Closed(a:Temperature) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => Kelvin(100000)
      case Open(a:Temperature) => a
      case Closed(a:Temperature) => a
    }
    interval.dist(min, max, min / 10)
  }

  // Solar luminosity ranges for the Morgan-Keenan classification.
  // http://www.handprint.com/ASTRO/specclass.html
  // scalastyle:off magic.number
  lazy private val luminosities:Map[Char, Interval[Power]] = Map(
    'O' -> Interval.above(SolarLuminosities(53000)),
    'B' -> Interval(SolarLuminosities(54), SolarLuminosities(52500)),
    'A' -> Interval(SolarLuminosities(6.5), SolarLuminosities(54)),
    'F' -> Interval(SolarLuminosities(1.5), SolarLuminosities(6.5)),
    'G' -> Interval(SolarLuminosities(0.4), SolarLuminosities(1.5)),
    'K' -> Interval(SolarLuminosities(0.08), SolarLuminosities(0.4)),
    'M' -> Interval(SolarLuminosities(0.00031622776), SolarLuminosities(0.08)),
    'C' -> Interval(SolarLuminosities(0), SolarLuminosities(0.001)),
    'S' -> Interval(SolarLuminosities(0), SolarLuminosities(0.001)),
    'L' -> Interval(SolarLuminosities(0.00003981071), SolarLuminosities(0.00019952623)),
    'T' -> Interval(SolarLuminosities(0.00000630957), SolarLuminosities(0.00003162277))
  )
  // scalastyle:on magic.number

  def distPower(interval:Interval[Power]):Dist[Power] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => SolarLuminosities(0)
      case Open(a:Power) => a
      case Closed(a:Power) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => SolarLuminosities(100000)
      case Open(a:Power) => a
      case Closed(a:Power) => a
    }
    interval.dist(min, max, min / 10)
  }

  // Solar radius ranges for the Morgan-Keenan classification frequencies.
  // http://www.handprint.com/ASTRO/specclass.html
  // scalastyle:off magic.number
  lazy private val radii:Map[Char, Interval[Length]] = Map(
    'O' -> Interval.above(SolarRadii(6.6)),
    'B' -> Interval(SolarRadii(1.8), SolarRadii(6.6)),
    'A' -> Interval(SolarRadii(1.4), SolarRadii(1.8)),
    'F' -> Interval(SolarRadii(1.15), SolarRadii(1.4)),
    'G' -> Interval(SolarRadii(0.96), SolarRadii(1.15)),
    'K' -> Interval(SolarRadii(0.7), SolarRadii(0.96)),
    'M' -> Interval(SolarRadii(0), SolarRadii(0.7)),
    'C' -> Interval(SolarRadii(220), SolarRadii(550)),
    'S' -> Interval(SolarRadii(0), SolarRadii(0.7)),
    'L' -> Interval(SolarRadii(0), SolarRadii(0.2)),
    'T' -> Interval(SolarRadii(0), SolarRadii(0.2))
  )
  // scalastyle:on magic.number

  def distLength(interval:Interval[Length]):Dist[Length] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => SolarRadii(0)
      case Open(a:Length) => a
      case Closed(a:Length) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => SolarRadii(10)
      case Open(a:Length) => a
      case Closed(a:Length) => a
    }
    interval.dist(min, max, min / 10)
  }

  // Solar mass ranges for the Morgan-Keenan classification.
  // http://www.handprint.com/ASTRO/specclass.html
  // scalastyle:off magic.number
  lazy private val masses:Map[Char, Interval[Mass]] = Map(
    'O' -> Interval.above(SolarMasses(18)),
    'B' -> Interval(SolarMasses(2.9), SolarMasses(18)),
    'A' -> Interval(SolarMasses(1.6), SolarMasses(2.9)),
    'F' -> Interval(SolarMasses(1.05), SolarMasses(1.6)),
    'G' -> Interval(SolarMasses(0.8), SolarMasses(1.05)),
    'K' -> Interval(SolarMasses(0.5), SolarMasses(0.8)),
    'M' -> Interval(SolarMasses(0.07), SolarMasses(0.5)),
    'C' -> Interval(SolarMasses(0), SolarMasses(1.1)),
    'S' -> Interval(SolarMasses(0), SolarMasses(0.8)),
    'L' -> Interval(SolarMasses(0.075), SolarMasses(0.45)),
    'T' -> Interval(SolarMasses(0.012), SolarMasses(0.075))
  )
  // scalastyle:on magic.number

  def distMass(interval:Interval[Mass]):Dist[Mass] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => SolarMasses(0)
      case Open(a:Mass) => a
      case Closed(a:Mass) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => SolarMasses(100)
      case Open(a:Mass) => a
      case Closed(a:Mass) => a
    }
    interval.dist(min, max, min / 10)
  }

  trait Instances {
    implicit val morganKeenanHasDist:Dist[MorganKeenan] = Dist.weightedMix[MorganKeenan](
      frequencies.toSeq.map({ case (c, d) =>
        (d.doubleValue(), Dist.constant(fromClassification(c).get))
      }):_*
    )
  }
  object instances extends Instances
}
