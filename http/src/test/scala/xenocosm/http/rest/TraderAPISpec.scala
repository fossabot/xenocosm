package xenocosm.http
package rest

import cats.effect.IO
import io.circe.Json
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import spire.random.rng.Serial

import xenocosm.data.{Identity, ShipModules, Trader}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.response.{TraderResponse, TradersResponse}
import xenocosm.http.services.MemoryDataStore

class TraderAPISpec extends xenocosm.test.XenocosmWordSpec with HttpCheck {
  import TraderResponse.instances._
  import TradersResponse.instances._

  val identity:Identity = xenocosm.gen.identity.sample.get.copy(trader = None)
  val trader:Trader = xenocosm.gen.trader.sample.get

  "GET /" when {
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = Uri.uri("/")))

    "identity has no traders" should {
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON-HAL" in {
        checkBody[Json](response) shouldBe Some(TradersResponse(List.empty[Trader]).asJson)
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

    }

    "identity has one or more traders" should {
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identity)
        _ <- data.createTrader(identity, trader)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON-HAL" in {
        checkBody[Json](response) shouldBe Some(TradersResponse(List(trader)).asJson)
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

    }
  }

  "POST /" when {
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.POST, uri = Uri.uri("/")))

    "trader created" should {
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 201 status" in {
        checkStatus(response) shouldBe Status.Created
      }

      "respond with JSON-HAL" in {
        checkBody[Json](response).flatMap(_.as[TraderResponse].toOption) match {
          case Some(res) => res shouldBe a[TraderResponse]
          case None => fail("expected a TraderResponse")
        }
      }

      "store the new trader" in {
        checkBody[Json](response).flatMap(_.as[TraderResponse].toOption) match {
          case None => fail("expected a TraderResponse")
          case Some(res) => data.selectTrader(identity.uuid, res.trader.uuid).unsafeRunSync() match {
            case None => fail("expected a Trader")
            case Some(randomTrader) => randomTrader.ship.modules shouldBe ShipModules.startingLoad
          }
        }
      }

      "select the new trader" in {
        checkBody[Json](response).flatMap(_.as[TraderResponse].toOption) match {
          case None => fail("expected a TraderResponse")
          case Some(res) => data.selectIdentity(identity.uuid).unsafeRunSync() match {
            case None => fail("expected an Identity")
            case Some(updated) => updated.trader shouldBe Some(res.trader)
          }
        }
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

    }

    "trader not created" should {

      "respond with 204 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "any other error" should {

      "respond with 500 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }
  }

  "GET /:uuid" when {
    val trader = xenocosm.gen.trader.sample.get
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = Uri.uri("/") / ⎈(trader.uuid)))

    "trader does not exist" should {

      "respond with 404 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "trader selected" should {

      "respond with 200 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }
  }

  "POST /:uuid" when {
    val trader = xenocosm.gen.trader.sample.get
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.POST, uri = Uri.uri("/") / ⎈(trader.uuid)))

    "trader does not exist" should {

      "respond with 404 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "trader selected" should {

      "respond with 200 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "trader not selected" should {

      "respond with 404 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "any other error" should {

      "respond with 500 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }
  }

  "DELETE /:uuid" when {
    val trader = xenocosm.gen.trader.sample.get
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.DELETE, uri = Uri.uri("/") / ⎈(trader.uuid)))

    "trader does not exist" should {

      "respond with 404 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "trader deleted" should {

      "respond with 204 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }

    "any other error" should {

      "respond with 500 status" in {
        fail("not implemented")
      }

      "refresh auth token" in {
        fail("not implemented")
      }

    }
  }
}
