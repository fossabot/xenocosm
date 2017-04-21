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

  val screenStart:fansi.Str =
    (0 to 255).
      map(i ⇒ fansi.Color.True(i, 255 - i, 255)(".")).
      grouped(32).
      map(_.mkString).
      mkString("\n")

  def locUniverse(req:Request, path:Uri.Path):headers.Location =
    headers.Location(req.uri.withPath(path))

  def locUniverse(req:Request, universe:Universe):headers.Location =
    locUniverse(req, new Uri.Path(s"/multiverse/${universe.uuid.toString}/0,0,0"))

  def locUniverse(req:Request):headers.Location =
    locUniverse(req, Universe(UUID.randomUUID))

  val service = HttpService {
    case req @ GET -> Root ⇒
      getSeed(req) match {
        case Some(_) ⇒
          Ok(screenStart).
            putHeaders(locUniverse(req))
        case None ⇒
          val seed = Random.nextLong()
          Ok(screenStart).
            addCookie(Cookie("seed", seed.toString)).
            putHeaders(locUniverse(req))
      }
  }
}
