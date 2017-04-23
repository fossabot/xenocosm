package xenocosm
package app
package service

import java.util.UUID

import scala.util.matching.Regex
import org.http4s._
import org.http4s.dsl._
import squants.space.Parsecs
import xenocosm.instances.interop._
import xenocosm.universe.data.Universe

object UniverseService {

  val regex:Regex = """([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})""".r

  def show(universe:Universe):String =
    """A Universe
      |  Age: %e yrs
      |  Diameter: %s
    """.stripMargin.format(
      universe.age.toDouble,
      universe.diameter.toString(Parsecs, "%e")
    )

  def screen(universe:Universe):fansi.Str =
    show(universe).
      split("\n").
      map(_.zipWithIndex.map({ case (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c") }).mkString).
      mkString("\n")

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / uuid ⇒
      (getSeed(req), uuid) match {
        case (_, regex(uuidStr)) ⇒
          val universe = Universe(UUID.fromString(uuidStr))
          Ok(screen(universe)).putHeaders(headers.Location(req.uri / "0,0,0"))
        case _ ⇒ NotFound()
      }
  }
}
