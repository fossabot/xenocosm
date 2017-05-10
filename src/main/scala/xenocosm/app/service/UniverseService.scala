package xenocosm
package app
package service

import org.http4s._
import org.http4s.dsl._

import xenocosm.interop.instances._
import xenocosm.universe.data.Universe

object UniverseService {

  val path:Universe ⇒ Uri.Path = x ⇒ s"/multiverse/${x.uuid.toString}"

  val location: Request ⇒ Universe ⇒ headers.Location = req ⇒ universe ⇒
    headers.Location(req.uri.withPath(path(universe)))

  def discover(req:Request, universe:Universe):headers.Location =
    headers.Location(req.uri.withPath(path(universe) ++ "/0,0,0"))

  val service = HttpService {
    case req @ GET -> Root / "multiverse" / ♈(universe) ⇒
      Ok(screen.Universe(universe)).putHeaders(discover(req, universe))
  }
}
