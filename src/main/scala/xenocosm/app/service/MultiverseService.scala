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

  val screen:fansi.Str =
    fansi.Color.True(0, 255, 255)(
      """You behold the multiverse.
        |Your mind boggles and threatens to tear itself apart.
        |""".stripMargin
    )

  val service = HttpService {
    case req @ GET -> Root / "multiverse" ⇒
      Ok(screen).putHeaders(discover(req))
  }
}
