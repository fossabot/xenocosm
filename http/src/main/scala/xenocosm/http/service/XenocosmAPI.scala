package xenocosm.http.service

import java.util.UUID
import scala.util.Try
import scala.util.matching.Regex
import galaxique.data.{Point3, Universe}
import org.http4s.{Charset, MediaType}
import org.http4s.headers.`Content-Type`
import squants.space.{AstronomicalUnits, Length, Parsecs}

trait XenocosmAPI {
  object UuidVal {
    def unapply(str:String):Option[Universe] =
      Try(UUID.fromString(str)).toOption.map(Universe.apply)
  }

  trait Point3Val {
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
  }

  def point3Segment(in:Length):Point3Val =
    new Point3Val {
      val scale:Length = in
    }

  val ♠ = UuidVal
  val ♣ = point3Segment(Parsecs(1))
  val ♥ = point3Segment(Parsecs(1))
  val ♦ = point3Segment(AstronomicalUnits(1))

  val jsonHal = `Content-Type`(MediaType.`application/hal+json`, Charset.`UTF-8`)
}
