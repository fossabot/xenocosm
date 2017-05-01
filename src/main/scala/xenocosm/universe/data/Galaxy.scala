package xenocosm
package universe
package data

import cats.Eq
import squants.energy._
import squants.space._
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.thermal.Temperature

import xenocosm.app.config
import xenocosm.geometry.data.{Point3, SparseSpace3}
import xenocosm.phonology.data.Phonology
import xenocosm.phonology.instances._
import HubbleSequence.instances._
import MorganKeenan.instances._

final case class Galaxy(universe:Universe, loc:Point3) { self ⇒
  private val gen:Generator = Galaxy.gen(self)
  private val mk:MorganKeenan = implicitly[Dist[MorganKeenan]].apply(gen)
  val hubbleSequence:HubbleSequence = implicitly[Dist[HubbleSequence]].apply(gen)
  val luminosity:Power = config.galaxy.luminosity.dist(gen)
  val diameter:Length = config.galaxy.diameter.dist(gen)
  val temperature:Temperature = MorganKeenan.temperatureRangeOf(mk)(gen)
  val phonology:Phonology = gen.next[Phonology]
}

object Galaxy {
  val bytes:Galaxy ⇒ Array[Byte] = galaxy ⇒
    Universe.bytes(galaxy.universe) ++ Point3.bytes(Parsecs)(galaxy.loc)

  val gen:Galaxy ⇒ Generator = BurtleRot2.fromBytes _ compose Digest.md5 compose bytes

  trait Instances {
    implicit val galaxyHasEq:Eq[Galaxy] = Eq.fromUniversalEquals[Galaxy]

    implicit val galaxyHasSparseSpace3:SparseSpace3[Galaxy, Star] =
      SparseSpace3.instance(Parsecs, Star.apply)(bytes)
  }

  object instances extends Instances
}
