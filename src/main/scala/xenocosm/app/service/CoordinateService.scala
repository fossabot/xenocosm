package xenocosm
package app
package service

import scala.util.matching.Regex
import org.http4s.{Header, Request, Uri, headers}
import squants.space.Length
import xenocosm.geometry.data.Point3

trait CoordinateService[A, B] {

  val scale:Length
  def scaled(x:String, y:String, z:String):Point3 =
    Point3((scale * x.toDouble).rint, (scale * y.toDouble).rint, (scale * z.toDouble).rint)
  def scaleHeader:Header = Header("X-Coordinate-Scale", scale.rint.toString(scale.unit))

  object Point3Val {
    val regex:Regex = """(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)""".r
    def unapply(str:String):Option[Point3] = str match {
      case regex(x, y, z) ⇒ Some(scaled(x, y, z))
      case _ ⇒ None
    }
  }

  def path(a:A)(loc:Point3):Uri.Path
  def path(loc:Point3):Uri.Path = {
    val x = loc.x.to(scale.unit).toLong / scale.to(scale.unit).toLong
    val y = loc.y.to(scale.unit).toLong / scale.to(scale.unit).toLong
    val z = loc.z.to(scale.unit).toLong / scale.to(scale.unit).toLong
    s"/$x,$y,$z"
  }

  def nearby(a:A, loc:Point3):Iterator[Point3]
  def nearbyLocations(req:Request, a:A, loc:Point3):Seq[headers.Location] =
    nearby(a, loc).
      map(headers.Location.apply _ compose req.uri.withPath compose path(a)).
      toSeq
}
