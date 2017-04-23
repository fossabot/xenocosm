package xenocosm
package app
package service

import scala.util.matching.Regex
import org.http4s.{Header, Request, Uri, headers}
import squants.UnitOfMeasure
import squants.space.Length
import xenocosm.geometry.data.Point3

trait CoordinateService[A,B] {
  val regex:Regex = """(-?[0-9]+),(-?[0-9]+),(-?[0-9]+)""".r

  val scale:Length
  val scaleUOM:UnitOfMeasure[Length]
  def scaled(x:String, y:String, z:String):Point3 =
    Point3((scale * x.toDouble).rint, (scale * y.toDouble).rint, (scale * z.toDouble).rint)
  def scaleHeader:Header = Header("X-Coordinate-Scale", scale.rint.toString(scaleUOM))

  def show(a:A):String
  def screen(a:A):fansi.Str
  def path(b:B)(loc:Point3):Uri.Path
  def path(loc:Point3):Uri.Path = {
    val x = loc.x.to(scaleUOM).toLong / scale.to(scaleUOM).toLong
    val y = loc.y.to(scaleUOM).toLong / scale.to(scaleUOM).toLong
    val z = loc.z.to(scaleUOM).toLong / scale.to(scaleUOM).toLong
    new Uri.Path(s"/$x,$y,$z")
  }
  def nearby(b:B, loc:Point3):Iterator[Point3]

  def nearbyLocations(req:Request, b:B, loc:Point3):Seq[headers.Location] =
    nearby(b, loc).
      map(headers.Location.apply _ compose req.uri.withPath compose path(b)).
      toSeq
}
