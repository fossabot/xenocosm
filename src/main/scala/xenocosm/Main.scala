package xenocosm

import scala.concurrent.ExecutionContext
import cats.implicits._
import fs2.{Strategy, Stream, Task}
import org.http4s.HttpService
import org.http4s.server.blaze._
import org.http4s.util.StreamApp

import xenocosm.app.client._
import xenocosm.app.middleware._
import xenocosm.app.service._

object Main extends StreamApp {
  import ExecutionContext.Implicits.global
  java.security.Security.setProperty("networkaddress.cache.ttl", "60")
  implicit val strategy:Strategy = Strategy.fromExecutionContext(implicitly[ExecutionContext])
  val riak = new RiakClientWrapper(app.config.riak)

  val middleware:HttpService ⇒ HttpService = ServerHeader.wrap

  val service:HttpService =
    HomeService.service |+|
    MultiverseService.service |+|
    UniverseService.service |+|
    IntergalacticCoordinateService.service |+|
    InterstellarCoordinateService.service |+|
    InterplanetaryCoordinateService.service

  val acquire:Task[Unit] = Task.fromFuture { riak.startup }

  val use:Unit ⇒ Stream[Task, Nothing] = _ ⇒ {
    BlazeBuilder
      .bindHttp(app.config.http.port, app.config.http.host)
      .mountService(middleware(service), "/")
      .serve
  }

  val release:Unit ⇒ Task[Unit] = _ ⇒ Task.fromFuture(riak.shutdown.map(_ ⇒ ()))

  override def stream(args:List[String]):Stream[Task, Nothing] =
    Stream.bracket(acquire)(use, release)
}
