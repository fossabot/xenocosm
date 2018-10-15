package xenocosm.http
package rest

import java.util.UUID
import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._
import squants.space.{AstronomicalUnits, Parsecs}

import galaxique.data.Point3
import xenocosm.data.{CosmicLocation, Identity, Ship, Trader}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.MemoryDataStore

class MultiverseAPISpec extends xenocosm.test.XenocosmWordSpec with HttpCheck {
  val data = new MemoryDataStore()
  val auth = XenocosmAuthentication("test", data)

  private def makeIdentity(uuid:UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                           locU:Option[Point3] = Some(Point3(Parsecs(-10000), Parsecs(-10000), Parsecs(0))),
                           locG:Option[Point3] = Some(Point3(Parsecs(0), Parsecs(-1), Parsecs(0))),
                           locS:Option[Point3] = Some(Point3(AstronomicalUnits(-1), AstronomicalUnits(0), AstronomicalUnits(-1)))): Identity = {
    val ship:Ship = xenocosm.gen.ship.sample.get.copy(loc = CosmicLocation(uuid, locU, locG, locS))
    val trader:Trader = xenocosm.gen.trader.sample.get.copy(ship = ship)
    val identity:Identity = xenocosm.gen.identity.sample.get.copy(trader = Some(trader))

    (for {
      _ <- data.createIdentity(identity)
      _ <- data.createTrader(identity, trader)
    } yield ()).unsafeRunSync()

    identity
  }


  "GET /:universeID" when {
    val service:AuthedService[Identity, IO] = new MultiverseAPI(auth, data).service

    "trader is in the same universe" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "trader is in a different universe" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/0000000000000000000000")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }

      "respond with empty body" in {
        checkBody[Json](response) shouldBe None
      }
    }
  }

  "GET /:universeID/:locU" when {
    val service:AuthedService[Identity, IO] = new MultiverseAPI(auth, data).service

    "trader is in the same galaxy" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "trader is in a different galaxy" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }

      "respond with empty body" in {
        checkBody[Json](response) shouldBe None
      }
    }
  }

  "GET /:universeID/:locU/:locG" when {
    val service:AuthedService[Identity, IO] = new MultiverseAPI(auth, data).service

    "trader is in the same star system" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "trader is in a different star system" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,0,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }

      "respond with empty body" in {
        checkBody[Json](response) shouldBe None
      }
    }
  }

  "GET /:universeID/:locU/:locG/:locS" when {
    val service:AuthedService[Identity, IO] = new MultiverseAPI(auth, data).service

    "trader is at the same planet" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/-1,0,-1")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "trader is at a different planet" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/0,0,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }

      "respond with empty body" in {
        checkBody[Json](response) shouldBe None
      }
    }
  }
}
