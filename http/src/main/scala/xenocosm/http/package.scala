package xenocosm

import galaxique.data.{Galaxy, Star, Universe}
import org.http4s.{Charset, MediaType, Uri}
import org.http4s.headers.`Content-Type`
import squants.space.{AstronomicalUnits, Parsecs}

package object http {
  val majorVersion:String = buildinfo.BuildInfo.version.split(".").headOption.getOrElse("X")
  val apiRoot:Uri = Uri.unsafeFromString(s"/v$majorVersion")
  val apiMultiverse:Uri = apiRoot / "multiverse"
  val apiTrader:Uri = apiRoot / "trader"
  val jsonHal = `Content-Type`(MediaType.`application/hal+json`, Charset.`UTF-8`)

  val ⎈ = UuidSegment
  val ✺ = Point3Segment(Universe.scale, Parsecs)
  val ✨ = Point3Segment(Galaxy.scale, Parsecs)
  val ★ = Point3Segment(Star.scale, AstronomicalUnits)
}
