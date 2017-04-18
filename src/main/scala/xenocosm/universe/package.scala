package xenocosm

import java.security.MessageDigest

package object universe {
  private val digest:MessageDigest = MessageDigest.getInstance("SHA-256")

  def proof(bytes:Array[Byte]):Option[Array[Byte]] = {
    val hash = digest.digest(bytes)
    hash.lastOption.filter(_ % 8 == 0).map(_ ⇒ hash)
  }
}
