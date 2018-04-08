package xenocosm.http
package data

import java.nio._
import java.util.UUID
import java.util.Base64
import java.nio.charset.StandardCharsets
import scala.util.Try

object UuidSegment {
  lazy val decoder:Base64.Decoder = Base64.getUrlDecoder
  lazy val encoder:Base64.Encoder = Base64.getUrlEncoder.withoutPadding
  def unapply(str:String):Option[UUID] =
    Try({
      val buffer = ByteBuffer.allocate(24).put((str + "==").getBytes(StandardCharsets.UTF_8))
      buffer.rewind()
      val decoded = decoder.decode(buffer)
      new UUID(decoded.getLong(), decoded.getLong())
    }).toOption

  def apply(uuid:UUID):String = {
    val buffer =
      ByteBuffer
        .allocate(16)
        .putLong(uuid.getMostSignificantBits)
        .putLong(uuid.getLeastSignificantBits)
    encoder.encodeToString(buffer.array())
  }
}
