package xenocosm
package app
package service

import java.util.UUID
import scala.util.matching.Regex
import org.http4s._
import org.http4s.dsl._
import squants.space.Parsecs
import xenocosm.geometry.data.Point3
import xenocosm.instances.interop._
import xenocosm.universe.data.Universe


object UniverseService {

  val uuidRegex:Regex = """([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})""".r
  val locRegex:Regex = """([0-9]+),([0-9]+),([0-9]+)""".r

  val screenIntergalacticSpace:fansi.Str =
    (0 to 255).
      map(i ⇒ fansi.Color.True(i, 255 - i, 255)(".")).
      grouped(32).
      map(_.mkString).
      mkString("\n")

  val screenGalaxy:fansi.Str =
    (0 to 255).
      map(i ⇒ fansi.Color.True(i, 255 - i, 255)("#")).
      grouped(32).
      map(_.mkString).
      mkString("\n")

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / uuid / loc ⇒
      (getSeed(req), uuid, loc) match {
        case (_, uuidRegex(uuidStr), locRegex(x, y, z)) ⇒
          val universe = Universe(UUID.fromString(uuidStr))
          val loc = Point3(Parsecs(x.toLong), Parsecs(y.toLong), Parsecs(z.toLong))
          xenocosm.universe.proof(universe, loc) match {
            case Some(_) ⇒ Ok(screenGalaxy)
            case None ⇒ Ok(screenIntergalacticSpace)
          }
        case _ ⇒ NotFound()
      }
  }
}
