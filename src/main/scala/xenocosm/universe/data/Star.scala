package xenocosm
package universe
package data

import cats.Eq
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.energy.Power
import squants.mass.{Density, Mass}
import squants.space.{AstronomicalUnits, Length, Parsecs, Volume}
import squants.thermal.Temperature

import xenocosm.geometry.data.{Point3, SparseSpace3}
import xenocosm.phonology.data.Phonology
import xenocosm.phonology.instances._
import MorganKeenan.instances._

final case class Star(galaxy:Galaxy, loc:Point3) { self ⇒
  private val gen:Generator = Star.gen(self)
  val morganKeenan:MorganKeenan = implicitly[Dist[MorganKeenan]].apply(gen)
  val mass:Mass = MorganKeenan.massRangeOf(morganKeenan)(gen)
  val luminosity:Power = MorganKeenan.powerRangeOf(morganKeenan)(gen)
  val radius:Length = MorganKeenan.radiusRangeOf(morganKeenan)(gen)
  val temperature:Temperature = MorganKeenan.temperatureRangeOf(morganKeenan)(gen)
  val μ:Double = mass.toKilograms * G
  val phonology:Phonology = gen.next[Phonology]
  val volume:Volume = (radius.cubed * Math.PI * 4) / 3
  val density:Density = mass / volume
}

object Star {
  val bytes:Star ⇒ Array[Byte] = star ⇒
    Galaxy.bytes(star.galaxy) ++ Point3.bytes(Parsecs)(star.loc)

  val gen:Star ⇒ Generator = BurtleRot2.fromBytes _ compose Digest.md5 compose bytes

  trait Instances {
    implicit val starHasEq:Eq[Star] = Eq.fromUniversalEquals[Star]

    implicit val starHasSparseSpace3:SparseSpace3[Star, StellarSystemBody] =
      new SparseSpace3[Star, StellarSystemBody] {
        val locate:Star ⇒ Point3 ⇒ Option[StellarSystemBody] = star ⇒ loc ⇒ {
          val digest = Digest.sha256(bytes(star) ++ Point3.bytes(AstronomicalUnits)(loc))
          digest.headOption.filter(_ % 8 == 0) map {
            case b if b % 64 == 0 ⇒ Planet(star, loc)
            case b if b % 64 == 1 ⇒ DwarfPlanet(star, loc)
            case _ ⇒ SmallBody(star, loc)
          }
        }
      }
  }
  object instances extends Instances
}
