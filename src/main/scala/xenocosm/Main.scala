package xenocosm

import scala.util.{Random, Try}
import fs2.{Stream, Task}
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers
import org.http4s.server.blaze._
import org.http4s.util.StreamApp
import spire.random.Dist
import spire.random.rng.Lcg64

import xenocosm.instances.interop._
import xenocosm.universe.data.Universe

import Universe.instances._

object Main extends StreamApp {

  val startScreen:fansi.Str =
    (0 to 255).
      map(i ⇒ fansi.Color.True(i, 255 - i, 255)(".")).
      grouped(32).
      map(_.mkString).
      mkString("\n")

  val game = HttpService {
    case req @ GET -> Root ⇒
      (for {
        cookies ← req.headers.get(headers.Cookie)
        cookie ← cookies.values.filter(_.name == "seed").headOption
        seed ← Try(cookie.content.toLong).toOption
      } yield seed) match {
        case Some(seed) ⇒
          val gen = Lcg64.fromSeed(seed)
          val universe = implicitly[Dist[Universe]].apply(gen)
          Ok(startScreen).
            putHeaders(headers.Location(req.uri.withPath(new Uri.Path(s"/universe/${universe.uuid.toString}"))))
        case None ⇒
          val seed = Random.nextLong()
          val gen = Lcg64.fromSeed(seed)
          val universe = implicitly[Dist[Universe]].apply(gen)
          Ok(startScreen).
            addCookie(Cookie("seed", seed.toString)).
            putHeaders(headers.Location(req.uri.withPath(new Uri.Path(s"/universe/${universe.uuid.toString}"))))
      }
  }

  override def main(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(8080, "localhost")
      .mountService(game, "/")
      .serve
  }
}
