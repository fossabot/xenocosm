package xenocosm.http

import scala.concurrent.ExecutionContext
import cats.effect.IO
import cats.implicits._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s._
import org.http4s.server.blaze._
import org.http4s.server.middleware.GZip


object Main extends StreamApp[IO] {
  import ExecutionContext.Implicits.global
  import service._

  java.security.Security.setProperty("networkaddress.cache.ttl", "60")

  val config:XenocosmConfig = XenocosmConfig.loadUnsafe

  val services:HttpService[IO] =
    MultiverseAPI.service <+> UniverseAPI.service

  val gzip:HttpService[IO] => HttpService[IO] = http => GZip(http)

  val wrapper:HttpService[IO] => HttpService[IO] =
    gzip compose middleware.ServerHeader.wrap

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(config.http.port, config.http.host)
      .mountService(wrapper(services), "/")
      .serve
}
