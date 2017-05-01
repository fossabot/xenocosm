package xenocosm
package app
package service

import org.http4s._
import org.http4s.dsl._
import squants.space.Parsecs

import xenocosm.interop.instances._
import xenocosm.universe.data.Universe

object UniverseService {

  val path:Universe ⇒ Uri.Path = x ⇒ s"/multiverse/${x.uuid.toString}"

  val location: Request ⇒ Universe ⇒ headers.Location = req ⇒ universe ⇒
    headers.Location(req.uri.withPath(path(universe)))

  def discover(req:Request, universe:Universe):headers.Location =
    headers.Location(req.uri.withPath(path(universe) ++ "/0,0,0"))

  private def screen(universe:Universe):fansi.Str =
    fansi.Color.True(16, 255 - 16, 255)(
      """A Universe
        |  Age: %e yrs
        |  Diameter: %s
        |""".stripMargin.format(
        universe.age.toDouble,
        universe.diameter.toString(Parsecs, "%e")
      )
    )

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) ⇒
      Ok(screen(universe)).putHeaders(discover(req, universe))
  }
}
