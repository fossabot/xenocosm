package xenocosm

import scala.concurrent.ExecutionContext
import cats.effect.IO
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s._
import org.http4s.server.middleware.GZip
import org.http4s.server.blaze._

import xenocosm.app.service._

object Main extends StreamApp[IO] {
  import ExecutionContext.Implicits.global
  java.security.Security.setProperty("networkaddress.cache.ttl", "60")

  val services:HttpService[IO] =
    HomeService.service

  val gzip:HttpService[IO] => HttpService[IO] = http => GZip(http)

  val wrapper:HttpService[IO] => HttpService[IO] =
    gzip compose app.middleware.ServerHeader.wrap

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(wrapper(services), "/")
      .serve
}
