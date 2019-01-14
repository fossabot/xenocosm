package xenocosm.http
package rest

import java.util.UUID
import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._
import squants.motion.CubicMetersPerSecond
import squants.space.{AstronomicalUnits, CubicMeters, KiloParsecs, Parsecs}
import squants.time.Seconds

import galaxique.data.Point3
import xenocosm.data._
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.MemoryDataStore

class MultiverseAPISpec extends xenocosm.test.XenocosmWordSpec with HttpCheck {
  val data = new MemoryDataStore()
  val auth = XenocosmAuthentication("test", data)

  private val defaultModules:ShipModules = List(
    FuelTank(CubicMeters(0), CubicMeters(100)),
    Navigation(AstronomicalUnits(1)),
    Engine(AstronomicalUnits(1) / Seconds(.01), CubicMetersPerSecond(1))
  )

  private def makeIdentity(uuid:UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                           locU:Option[Point3] = Some(Point3(KiloParsecs(-10), KiloParsecs(-10), Parsecs(0))),
                           locG:Option[Point3] = Some(Point3(Parsecs(0), Parsecs(-1), Parsecs(0))),
                           locS:Option[Point3] = Some(Point3(AstronomicalUnits(-1), AstronomicalUnits(0), AstronomicalUnits(-1))),
                           modules:ShipModules = defaultModules): Identity = {
    val ship:Ship = xenocosm.gen.ship.sample.get.copy(
      loc = CosmicLocation(uuid, locU, locG, locS),
      modules = modules)
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
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
      }
    }

    "trader is in a different universe" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000001")
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
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
      }
    }

    "trader is in a different galaxy" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/0,0,0")
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
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,-1,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
      }
    }

    "trader is in a different star system" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,0,0")
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
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,-1,0/-1,0,-1")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
      }
    }

    "trader is at a different planet" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,-1,0/0,0,0")
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

  "POST /:universeID/:locU/:locG/:locS" when {
    val service:AuthedService[Identity, IO] = new MultiverseAPI(auth, data).service

    "ship can make it to the destination" should {
      val identity = makeIdentity()
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,-1,0/-1,0,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.POST, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
      }
    }

    "ship cannot navigate the distance" should {
      val modules:ShipModules = List(
        FuelTank(CubicMeters(0), CubicMeters(100)),
        Navigation(AstronomicalUnits(0.5)),
        Engine(AstronomicalUnits(1) / Seconds(.01), CubicMetersPerSecond(1))
      )
      val identity = makeIdentity(modules=modules)
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,-1,0/-1,0,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.POST, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
        maybeBody.get.hcursor.downField("error").as[String] shouldBe Right("Cannot navigate to location. Maximum distance is 7.90625370491033E-6 ly")
      }
    }

    "ship has insufficient fuel" should {
      val modules:ShipModules = List(
        FuelTank(CubicMeters(0), CubicMeters(0)),
        Navigation(AstronomicalUnits(1)),
        Engine(AstronomicalUnits(1) / Seconds(.01), CubicMetersPerSecond(1))
      )
      val identity = makeIdentity(modules=modules)
      val uri = Uri.uri("/00000000-0000-0000-0000-000000000000/-10,-10,0/0,-1,0/-1,0,0")
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.POST, uri = uri))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }

      "respond with JSON body" in {
        val maybeBody = checkBody[Json](response)
        maybeBody shouldBe defined
        maybeBody.get shouldBe a[Json]
        maybeBody.get.hcursor.downField("error").as[String] shouldBe Right("Not enough fuel. Remaining fuel is 0.0 m³")
      }
    }
  }
}
