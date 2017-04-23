package xenocosm
package app
package service

import java.util.UUID
import scala.util.Random
import org.http4s._
import org.http4s.dsl._

import xenocosm.instances.interop._
import xenocosm.universe.data.Universe

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

  def locUniverse(req:Request, path:Uri.Path):headers.Location =
    headers.Location(req.uri.withPath(path))

  def locUniverse(req:Request, universe:Universe):headers.Location =
    locUniverse(req, new Uri.Path(s"/multiverse/${universe.uuid.toString}"))

  def locUniverse(req:Request):headers.Location =
    locUniverse(req, Universe(UUID.randomUUID))

  val service = HttpService {
    case req @ GET -> Root ⇒
      getSeed(req) match {
        case Some(_) ⇒
          Ok(screen).
            putHeaders(locUniverse(req))
        case None ⇒
          val seed = Random.nextLong()
          Ok(screen).
            addCookie(Cookie("seed", seed.toString)).
            putHeaders(locUniverse(req))
      }
  }
}
