package xenocosm.http
package data

import java.util.UUID
import scala.util.Try
import galaxique.data.Universe

object UuidSegment {
  def unapply(str:String):Option[Universe] =
    Try(UUID.fromString(str)).toOption.map(Universe.apply)

  def apply(universe:Universe):String = s"${universe.uuid.toString}"
}
