package xenocosm

import java.nio.ByteBuffer
import java.security.MessageDigest

import xenocosm.geometry.data.Point3
import xenocosm.universe.data.{Galaxy, Universe}

package object universe {

  def proof(bytes:Array[Byte]):Option[Byte] =
    MessageDigest.
      getInstance("SHA-256").
      digest(bytes).
      lastOption.
      filter(_ % 8 == 0)

  def proof(universe:Universe, loc:Point3):Option[Point3] =
    proof {
      ByteBuffer.
        allocate(40).
        putLong(universe.uuid.getMostSignificantBits).
        putLong(universe.uuid.getLeastSignificantBits).
        putDouble(loc.x.toParsecs).
        putDouble(loc.y.toParsecs).
        putDouble(loc.z.toParsecs).
        array()
    } map (_ ⇒ loc)

  def proof(galaxy:Galaxy, loc:Point3):Option[Point3] =
    proof {
      ByteBuffer.
        allocate(48).
        putDouble(galaxy.loc.x.toParsecs).
        putDouble(galaxy.loc.y.toParsecs).
        putDouble(galaxy.loc.z.toParsecs).
        putDouble(loc.x.toParsecs).
        putDouble(loc.y.toParsecs).
        putDouble(loc.z.toParsecs).
        array()
    } map (_ ⇒ loc)
}
