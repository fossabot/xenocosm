package galaxique

import cats.implicits._
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
  description:String,
  temperature:Interval[Temperature],
  mass:Interval[Mass],
  radius:Interval[Length],
  luminosity:Interval[Power],
  frequency:Double) extends Product with Serializable {
  def distTemperature:Dist[Temperature] = MorganKeenan.distTemperature(temperature)
  def distMass:Dist[Mass] = MorganKeenan.distMass(mass)
  def distRadius:Dist[Length] = MorganKeenan.distRadius(radius)
  def distLuminosity:Dist[Power] = MorganKeenan.distLuminosity(luminosity)
}

object MorganKeenan {
  import interop.length._
  import interop.mass._
  import interop.power._
  import interop.temperature._

  def fromClassification(classification:Char):Option[MorganKeenan] =
    Observed.all.find(_.classification === classification)

  def fromClassificationEither(classification:Char):Either[String, MorganKeenan] =
    fromClassification(classification) match {
      case Some(mk) => Right(mk)
      case None => Left(s"Unsupported Morgan-Keenan classification '${classification.toString}'.")
    }

  /**
    * Observed frequencies of MK classified stars
    *
    * @see http://www.handprint.com/ASTRO/specclass.html
    * scalastyle:off magic.number
    */
  object Observed {
    lazy val all:Seq[MorganKeenan] = Seq(W, O, B, A, F, G, K, M, C, S, L, T, Y, D)

    lazy val W:MorganKeenan =
      MorganKeenan('W', "Wolf-Rayet",
        Interval.above(Kelvin(25000)),
        Interval.above(SolarMasses(20)),
        Interval(SolarRadii(10), SolarRadii(15)),
        Interval.above(SolarLuminosities(1e5)),
        2e-8d)
    lazy val O:MorganKeenan =
      MorganKeenan('O', "super massive",
        Interval.above(Kelvin(30000)),
        Interval(SolarMasses(18), SolarMasses(150)),
        Interval.above(SolarRadii(6.6)),
        Interval(SolarLuminosities(53000), SolarLuminosities(1e6)),
        0.23d)
    lazy val B:MorganKeenan =
      MorganKeenan('B', "massive",
        Interval(Kelvin(10000), Kelvin(30000)),
        Interval(SolarMasses(2.9), SolarMasses(18)),
        Interval(SolarRadii(1.8), SolarRadii(6.6)),
        Interval(SolarLuminosities(54), SolarLuminosities(52500)),
        8.9d)
    lazy val A:MorganKeenan =
      MorganKeenan('A', "large",
        Interval(Kelvin(7300), Kelvin(10000)),
        Interval(SolarMasses(1.6), SolarMasses(2.9)),
        Interval(SolarRadii(1.4), SolarRadii(1.8)),
        Interval(SolarLuminosities(6.5), SolarLuminosities(54)),
        16d)
    lazy val F:MorganKeenan =
      MorganKeenan('F', "solar type",
        Interval(Kelvin(6000), Kelvin(7300)),
        Interval(SolarMasses(1.05), SolarMasses(1.6)),
        Interval(SolarRadii(1.15), SolarRadii(1.4)),
        Interval(SolarLuminosities(1.5), SolarLuminosities(6.5)),
        22d)
    lazy val G:MorganKeenan =
      MorganKeenan('G', "solar type",
        Interval(Kelvin(5300), Kelvin(6000)),
        Interval(SolarMasses(0.8), SolarMasses(1.05)),
        Interval(SolarRadii(0.96), SolarRadii(1.15)),
        Interval(SolarLuminosities(0.4), SolarLuminosities(1.5)),
        19.6d)
    lazy val K:MorganKeenan =
      MorganKeenan('K', "solar type",
        Interval(Kelvin(3800), Kelvin(5300)),
        Interval(SolarMasses(0.5), SolarMasses(0.8)),
        Interval(SolarRadii(0.7), SolarRadii(0.96)),
        Interval(SolarLuminosities(0.08), SolarLuminosities(0.4)),
        27.6d)
    lazy val M:MorganKeenan =
      MorganKeenan('M', "sub solar",
        Interval(Kelvin(2500), Kelvin(3800)),
        Interval(SolarMasses(0.07), SolarMasses(0.5)),
        Interval.below(SolarRadii(0.7)),
        Interval(SolarLuminosities(Math.pow(10, -3.5)), SolarLuminosities(0.08)),
        5d)
    lazy val C:MorganKeenan =
      MorganKeenan('C', "carbon star",
        Interval(Kelvin(2400), Kelvin(3200)),
        Interval.below(SolarMasses(1.1)),
        Interval(SolarRadii(220), SolarRadii(550)),
        Interval.below(SolarLuminosities(1e-3)),
        1e-10d)
    lazy val S:MorganKeenan =
      MorganKeenan('S', "sub carbon star",
        Interval(Kelvin(2400), Kelvin(3500)),
        Interval.below(SolarMasses(0.8)),
        Interval.below(SolarRadii(0.7)),
        Interval.below(SolarLuminosities(1e-3)),
        0.14d)
    lazy val L:MorganKeenan =
      MorganKeenan('L', "hot brown dwarf",
        Interval(Kelvin(1300), Kelvin(2100)),
        Interval(SolarMasses(0.075), SolarMasses(0.45)),
        Interval.below(SolarRadii(0.2)),
        Interval(SolarLuminosities(Math.pow(10, -4.4)), SolarLuminosities(Math.pow(10, -3.7))),
        1e-10d)
    lazy val T:MorganKeenan =
      MorganKeenan('T', "cool brown dwarf",
        Interval(Kelvin(600), Kelvin(1300)),
        Interval(SolarMasses(0.012), SolarMasses(0.075)),
        Interval.below(SolarRadii(0.2)),
        Interval(SolarLuminosities(Math.pow(10, -5.2)), SolarLuminosities(Math.pow(10, -4.5))),
        1e-10d)
    lazy val Y:MorganKeenan =
      MorganKeenan('Y', "gas giant",
        Interval.below(Kelvin(600)),
        Interval.below(SolarMasses(0.012)),
        Interval.below(SolarRadii(0.15)),
        Interval.below(SolarLuminosities(Math.pow(10, -5.2))),
        1e-10d)
    lazy val D:MorganKeenan =
      MorganKeenan('D', "white dwarf",
        Interval.below(Kelvin(100000)),
        Interval(SolarMasses(0.17), SolarMasses(1.3)),
        Interval(SolarRadii(0.008), SolarRadii(0.02)),
        Interval(SolarLuminosities(1e-4), SolarLuminosities(100)),
        1e-10d)
  }
  // scalastyle:on magic.number

  lazy private val weightedDist:Seq[(Double, Dist[MorganKeenan])] =
    Observed.all.map(mk => mk.frequency -> Dist.constant(mk))

  private def distTemperature(interval:Interval[Temperature]):Dist[Temperature] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => Kelvin(0)
      case Open(a:Temperature) => a
      case Closed(a:Temperature) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => min + Kelvin(100)
      case Open(a:Temperature) => a
      case Closed(a:Temperature) => a
    }
    interval.dist(min, max, min / 10)
  }

  private def distLuminosity(interval:Interval[Power]):Dist[Power] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => SolarLuminosities(0)
      case Open(a:Power) => a
      case Closed(a:Power) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => min + SolarLuminosities(100)
      case Open(a:Power) => a
      case Closed(a:Power) => a
    }
    interval.dist(min, max, min / 10)
  }

  private def distRadius(interval:Interval[Length]):Dist[Length] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => SolarRadii(0)
      case Open(a:Length) => a
      case Closed(a:Length) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => min + SolarRadii(100)
      case Open(a:Length) => a
      case Closed(a:Length) => a
    }
    interval.dist(min, max, min / 10)
  }

  private def distMass(interval:Interval[Mass]):Dist[Mass] = {
    val min = interval.lowerBound match {
      case EmptyBound() | Unbound() => SolarMasses(0)
      case Open(a:Mass) => a
      case Closed(a:Mass) => a
    }
    val max = interval.upperBound match {
      case EmptyBound() | Unbound() => min + SolarMasses(100)
      case Open(a:Mass) => a
      case Closed(a:Mass) => a
    }
    interval.dist(min, max, min / 10)
  }

  trait Instances {
    implicit val morganKeenanHasDist:Dist[MorganKeenan] =
      Dist.weightedMix[MorganKeenan](weightedDist:_*)
  }
  object instances extends Instances
}
