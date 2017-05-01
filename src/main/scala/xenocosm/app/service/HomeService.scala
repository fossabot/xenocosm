package xenocosm
package app
package service

import scala.util.Random
import org.http4s._
import org.http4s.dsl._

import xenocosm.interop.instances._

object HomeService {

  val service = HttpService {
    case req @ GET -> Root ⇒
      getSeed(req) match {
        case Some(_) ⇒
          Ok(screen.Home.screen).
            putHeaders(MultiverseService.location(req))
        case None ⇒
          val seed = Random.nextLong()
          Ok(screen.Home.screen).
            addCookie(Cookie("seed", seed.toString)).
            putHeaders(MultiverseService.location(req))
      }
  }
}
