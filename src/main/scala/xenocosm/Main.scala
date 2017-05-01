package xenocosm

import cats.implicits._
import fs2.{Stream, Task}
import org.http4s.HttpService
import org.http4s.server.blaze._
import org.http4s.util.StreamApp

import xenocosm.app.middleware._
import xenocosm.app.service._

object Main extends StreamApp {

  val middleware:HttpService ⇒ HttpService = ServerHeader.wrap
  val service:HttpService =
    HomeService.service |+|
      MultiverseService.service |+|
      UniverseService.service |+|
      IntergalacticCoordinateService.service |+|
      InterstellarCoordinateService.service |+|
      InterplanetaryCoordinateService.service

  override def main(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(app.config.http.port, app.config.http.host)
      .mountService(middleware(service), "/")
      .serve
  }
}
