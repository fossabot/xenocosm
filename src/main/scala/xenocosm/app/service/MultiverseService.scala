package xenocosm
package app
package service

import org.http4s._
import org.http4s.dsl._

object MultiverseService {

  val service = HttpService {
    case req @ GET -> Root / "multiverse" ⇒
      Ok("You behold the multiverse. Your mind boggles and threatens to tear itself apart.").
        putHeaders(HomeService.locUniverse(req))
  }
}
