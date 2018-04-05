package xenocosm.http
package data

import scala.util.matching.Regex
import galaxique.data.Point3
import squants.space.Length

trait Point3Segment {
  val scale:Length
  val regex:Regex = """(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)""".r
  def unapply(str:String):Option[Point3] = str match {
    case regex(x, y, z) ⇒
      Some(Point3(
        (scale * x.toDouble).rint,
        (scale * y.toDouble).rint,
        (scale * z.toDouble).rint
      ))
    case _ ⇒ None
  }

  def apply(loc:Point3):String =
    s"${(loc.x / scale).longValue()},${(loc.y / scale).longValue()},${(loc.z / scale).longValue()}"
}

object Point3Segment {
  def apply(in:Length):Point3Segment =
    new Point3Segment {
      val scale:Length = in
    }
}
