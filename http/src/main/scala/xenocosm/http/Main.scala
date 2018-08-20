package xenocosm.http

import scala.concurrent.ExecutionContext
import cats.effect.IO
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s._
import org.http4s.server.blaze._
import org.http4s.server.middleware.GZip
import spire.random.Generator
import spire.random.rng.SecureJava

import xenocosm.data.Identity
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.rest._
import xenocosm.http.services.{DataStore, MemoryDataStore}

object Main extends StreamApp[IO] {
  import ExecutionContext.Implicits.global

  java.security.Security.setProperty("networkaddress.cache.ttl", "60")

  val config:XenocosmConfig = XenocosmConfig.loadUnsafe
  val data:DataStore = new MemoryDataStore()
  val auth:XenocosmAuthentication = XenocosmAuthentication(config.http.secret, data)

  val gzip:HttpService[IO] => HttpService[IO] = http => GZip(http)

  val unauthenticated:HttpService[IO] => HttpService[IO] =
    gzip compose middleware.ServerHeader.wrap

  val authenticated:AuthedService[Identity, IO] => HttpService[IO] =
    unauthenticated compose auth.wrap

  val gen:Generator = SecureJava.apply()

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(config.http.port, config.http.host)
      .mountService(unauthenticated(new AuthAPI(auth, data).service), "/auth")
      .mountService(authenticated(new TraderAPI(auth, data, gen).service), "/v1/trader")
      .mountService(authenticated(new MultiverseAPI(auth, data).service), "/v1/multiverse")
      .serve
}
