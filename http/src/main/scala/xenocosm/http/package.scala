package xenocosm

import galaxique.data.{Galaxy, Star, Universe}
import org.http4s.{Charset, MediaType}
import org.http4s.headers.`Content-Type`
import squants.space.{AstronomicalUnits, Parsecs}

package object http {
  val majorVersion:String = buildinfo.BuildInfo.version.split(".").headOption.getOrElse("X")
  val apiMultiverse:String = s"/v$majorVersion/multiverse"
  val apiTrader:String = s"/v$majorVersion/trader"
  val jsonHal = `Content-Type`(MediaType.`application/hal+json`, Charset.`UTF-8`)

  val ⎈ = UuidSegment
  val ✺ = Point3Segment(Universe.scale, Parsecs)
  val ✨ = Point3Segment(Galaxy.scale, Parsecs)
  val ★ = Point3Segment(Star.scale, AstronomicalUnits)
}
