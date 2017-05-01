package xenocosm
package universe
package data

import java.nio.ByteBuffer
import java.util.UUID
import cats.Eq
import spire.random.rng.BurtleRot2
import spire.random.Generator
import squants.space._

import xenocosm.app.config
import xenocosm.geometry.data.SparseSpace3

final case class Universe(uuid:UUID) { self ⇒
  private val gen:Generator = Universe.gen(self)
  val age:Long = config.universe.age.dist(gen)
  val diameter:Length = config.universe.diameter.dist(gen)
}

object Universe {

  val bytes:Universe ⇒ Array[Byte] = universe ⇒
    ByteBuffer.
      allocate(16).
      putLong(universe.uuid.getMostSignificantBits).
      putLong(universe.uuid.getLeastSignificantBits).
      array()

  val gen:Universe ⇒ Generator = BurtleRot2.fromBytes _ compose Digest.md5 compose bytes

  trait Instances {
    implicit val universeHasEq:Eq[Universe] = Eq.fromUniversalEquals[Universe]

    implicit val universeHasSparseSpace3:SparseSpace3[Universe, Galaxy] =
      SparseSpace3.instance(Parsecs, Galaxy.apply)(bytes)
  }
  object instances extends Instances
}
