package galaxique
package data

import java.nio.ByteBuffer
import java.util.UUID
import cats.Eq
import spire.math.Interval
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2
import squants.space.{GigaParsecs, KiloParsecs, Length, Parsecs}

final case class Universe(uuid:UUID) { self =>
  private val gen:Generator = Universe.gen(self)
  val age:Long = Universe.ageDist(gen)
  val diameter:Length = Universe.diameterDist(gen).in(Parsecs)
  lazy val radius:Length = (diameter / 2).in(Parsecs)
}

object Universe {
  import spire.implicits._
  import interop.squants.instances._
  import interop.java.instances._

  lazy val scale:Length = KiloParsecs(1)

  // scalastyle:off magic.number
  private[data] val bytes:Universe ⇒ Array[Byte] = universe ⇒
    ByteBuffer
      .allocate(16)
      .putLong(universe.uuid.getMostSignificantBits)
      .putLong(universe.uuid.getLeastSignificantBits)
      .array()
  // scalastyle:on magic.number
  private val gen:Universe => Generator = BurtleRot2.fromBytes _ compose bytes

  // Interesting things start happening after 9B yrs and stop after about 20B yrs
  // https://en.wikipedia.org/wiki/Timeline_of_the_formation_of_the_Universe
  // https://en.wikipedia.org/wiki/Ultimate_fate_of_the_universe
  private lazy val ageMin:Long = 9000000000L
  private lazy val ageMax:Long = 20000000000L
  private lazy val age:Interval[Long] = Interval(ageMin, ageMax)
  private lazy val ageDist:Dist[Long] = age.dist(ageMin, ageMax, ageMin / 1000)

  private lazy val diameterMin:Length = GigaParsecs(10)
  private lazy val diameterMax:Length = GigaParsecs(30)
  private lazy val diameter:Interval[Length] = Interval(diameterMin, diameterMax)
  private lazy val diameterDist:Dist[Length] = diameter.dist(diameterMin, diameterMax, diameterMin / 1000)

  // Scale a double from [0.0, 1.0) to correspond to a point within the universe
  private val toCoordinate:Universe => Double => Length = universe => d =>
    scale * ((universe.diameter * ((2 * d) - 1)) / scale).floor

  //FIXME: Calculate z-axis
  val point:Universe => Dist[Point3] = universe =>
    for {
      x <- Dist.double
      y <- Dist.double
    } yield Point3(
      toCoordinate(universe)(x),
      toCoordinate(universe)(y),
      Parsecs(0)
    )

  trait Instances {
    implicit val universeHasEq:Eq[Universe] = Eq.fromUniversalEquals[Universe]

    implicit val universeHasDist:Dist[Universe] =
      Dist[UUID].map(Universe.apply)

    implicit val universeHasSparseSpace:SparseSpace3[Universe, Galaxy] =
      SparseSpace3.fromStandardProof[Universe, Galaxy](Parsecs, scale)(Galaxy.apply)(bytes)
  }
  object instances extends Instances
}
