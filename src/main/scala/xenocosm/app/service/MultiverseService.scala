package xenocosm
package app
package service

import java.util.UUID
import org.http4s._
import org.http4s.dsl._

import xenocosm.interop.instances._
import xenocosm.universe.data.Universe

object MultiverseService {

  val path:Uri.Path = "/multiverse"
  val location:Request ⇒ headers.Location = req ⇒
    headers.Location(req.uri.withPath(path))

  val discover:Request ⇒ headers.Location = req ⇒ {
    val universe = Universe(UUID.randomUUID())
    headers.Location(req.uri.withPath(UniverseService.path(universe)))
  }

  val service = HttpService {
    case req @ GET -> Root / "multiverse" ⇒
      Ok(screen.Multiverse.screen).putHeaders(discover(req))
  }
}
