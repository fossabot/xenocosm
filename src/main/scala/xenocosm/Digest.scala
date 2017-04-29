package xenocosm

import java.security.MessageDigest

object Digest {
  val md5:Array[Byte] ⇒ Array[Byte] = MessageDigest.getInstance("MD5").digest _
  val sha256:Array[Byte] ⇒ Array[Byte] = MessageDigest.getInstance("SHA-256").digest _
}
