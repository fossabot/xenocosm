package xenocosm.http
package data

import scala.util.matching.Regex
import squants.UnitOfMeasure
import squants.space.{AstronomicalUnits, Length, Parsecs}
import galaxique.data.Point3

sealed abstract class Point3Segment(scale:Length, uom:UnitOfMeasure[Length]) {
  private val regex:Regex = """(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)""".r
  def unapply(str:String):Option[Point3] = str match {
    case regex(x, y, z) ⇒
      Some(Point3(
        (scale * x.toDouble).in(uom).floor,
        (scale * y.toDouble).in(uom).floor,
        (scale * z.toDouble).in(uom).floor
      ))
    case _ ⇒ None
  }

  def apply(loc:Point3):String =
    List(loc.x, loc.y, loc.z)
      .map(a => (a / scale).floor.toLong)
      .mkString(",")
}
object IntergalacticSegment extends Point3Segment(Parsecs(10000), Parsecs)
object InterstellarSegment extends Point3Segment(Parsecs(1), Parsecs)
object InterplanetarySegment extends Point3Segment(AstronomicalUnits(1), AstronomicalUnits)
