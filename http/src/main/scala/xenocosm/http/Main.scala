package xenocosm.http

import scala.concurrent.ExecutionContext
import cats.effect.IO
import cats.implicits._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s._
import org.http4s.server.blaze._
import org.http4s.server.middleware.GZip
import spire.random.Generator
import spire.random.rng.SecureJava

import xenocosm.http.service._

object Main extends StreamApp[IO] {
  import ExecutionContext.Implicits.global

  java.security.Security.setProperty("networkaddress.cache.ttl", "60")

  val config:XenocosmConfig = XenocosmConfig.loadUnsafe

  val gzip:HttpService[IO] => HttpService[IO] = http => GZip(http)

  val wrapper:HttpService[IO] => HttpService[IO] =
    gzip compose middleware.ServerHeader.wrap

  val gen:Generator = SecureJava.apply()

  val api:HttpService[IO] = wrapper {
    MultiverseAPI.service <+>
    TraderAPI.service(gen)
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(config.http.port, config.http.host)
      .mountService(api, "/")
      .serve
}
