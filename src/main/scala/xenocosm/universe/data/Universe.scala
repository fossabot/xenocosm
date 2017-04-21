package xenocosm
package universe
package data

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID
import cats.Eq
import spire.random.rng.BurtleRot2
import spire.random.Generator
import squants.space._

import xenocosm.app.config

final case class Universe(uuid:UUID) {
  private def bytes:Array[Byte] =
    ByteBuffer.
      allocate(16).
      putLong(uuid.getMostSignificantBits).
      putLong(uuid.getLeastSignificantBits).
      array()

  val digest:Array[Byte] = MessageDigest.getInstance("MD5").digest(bytes)
  private val gen:Generator = BurtleRot2.fromBytes(digest)

  val age:Long = config.universe.age.dist(10L)(gen)
  val diameter:Length = config.universe.diameter.dist(Parsecs(100))(gen)
}

object Universe {
  trait Instances {
    implicit val universeHasEq:Eq[Universe] = Eq.fromUniversalEquals[Universe]
  }
  object instances extends Instances
}
