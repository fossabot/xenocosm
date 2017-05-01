package xenocosm
package app
package service

import scala.util.Random
import org.http4s._
import org.http4s.dsl._

import xenocosm.interop.instances._

object HomeService {

  private val title:String =
    """################################################################
      |################################################################
      |#####     _  _____  ____  ____  _________  _________ ___   #####
      |#####    | |/_/ _ \/ __ \/ __ \/ ___/ __ \/ ___/ __ `__ \  #####
      |#####   _>  </  __/ / / / /_/ / /__/ /_/ (__  ) / / / / /  #####
      |#####  /_/|_|\___/_/ /_/\____/\___/\____/____/_/ /_/ /_/   #####
      |################################################################
      |################################################################""".stripMargin

  val screen:fansi.Str =
    title.
      split("\n").
      map(_.zipWithIndex.map({ case (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c") }).mkString).
      mkString("\n")

  val service = HttpService {
    case req @ GET -> Root ⇒
      getSeed(req) match {
        case Some(_) ⇒
          Ok(screen).putHeaders(MultiverseService.location(req))
        case None ⇒
          val seed = Random.nextLong()
          Ok(screen).
            addCookie(Cookie("seed", seed.toString)).
            putHeaders(MultiverseService.location(req))
      }
  }
}
