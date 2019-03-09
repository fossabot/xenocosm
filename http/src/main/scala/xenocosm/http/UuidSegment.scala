package xenocosm.http

import java.util.UUID
import scala.util.Try

object UuidSegment {
  def unapply(str:String):Option[UUID] =
    Try(UUID.fromString(str)).toOption

  def apply(uuid:UUID):String =
    uuid.toString
}
