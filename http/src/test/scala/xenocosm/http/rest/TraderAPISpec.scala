package xenocosm.http
package rest

import java.util.UUID
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

  private class CannotUpdateIdentity extends MemoryDataStore {
    override def updateIdentity(identity:Identity):IO[Unit] =
      IO.raiseError(new Exception("boom"))
  }

  private class CannotCreateTrader extends MemoryDataStore {
    override def createTrader(identity:Identity, trader:Trader):IO[Unit] =
      IO.raiseError(new Exception("boom"))
  }

  private class CannotFindTrader extends MemoryDataStore {
    override def selectTrader(identityID:UUID, traderID:UUID):IO[Option[Trader]] =
      IO.raiseError(new Exception("boom"))
  }

  private class CannotListTraders extends MemoryDataStore {
    override def listTraders(identity:Identity):IO[List[Trader]] =
      IO.raiseError(new Exception("boom"))
  }

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

    "cannot list traders" should {
      val gen = Serial(0L)
      val data = new CannotListTraders()

      (for {
        _ <- data.createIdentity(identity)
        _ <- data.createTrader(identity, trader)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
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

    "cannot create trader" should {
      val gen = Serial(0L)
      val data = new CannotCreateTrader()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "cannot update identity" should {
      val gen = Serial(0L)
      val data = new CannotUpdateIdentity()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }
  }

  "GET /:uuid" when {
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.GET, uri = Uri.uri("/") / ⎈(trader.uuid)))

    "trader does not exist" should {
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 404 status" in {
        checkStatus(response) shouldBe Status.NotFound
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "trader selected" should {
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
        checkBody[Json](response).flatMap(_.as[TraderResponse].toOption) match {
          case Some(res) => res shouldBe a[TraderResponse]
          case None => fail("expected a TraderResponse")
        }
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

    }

    "cannot select trader" should {
      val gen = Serial(0L)
      val data = new CannotFindTrader()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

    }
  }

  "POST /:uuid" when {
    val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.POST, uri = Uri.uri("/") / ⎈(trader.uuid)))

    "trader does not exist" should {
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 404 status" in {
        checkStatus(response) shouldBe Status.NotFound
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "trader selected" should {
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
        checkBody[Json](response).flatMap(_.as[TraderResponse].toOption) match {
          case Some(res) => res shouldBe a[TraderResponse]
          case None => fail("expected a TraderResponse")
        }
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
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
    }

    "cannot select trader" should {
      val gen = Serial(0L)
      val data = new CannotFindTrader()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "cannot update identity" should {
      val gen = Serial(0L)
      val data = new CannotUpdateIdentity()

      (for {
        _ <- data.createIdentity(identity)
        _ <- data.createTrader(identity, trader)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }
  }

  "DELETE /:uuid" when {

    "trader does not exist" should {
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.DELETE, uri = Uri.uri("/") / ⎈(trader.uuid)))
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "current trader deleted" should {
      val identityA = identity.copy(trader = Some(trader))
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identityA, Request(method = Method.DELETE, uri = Uri.uri("/") / ⎈(trader.uuid)))
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identityA)
        _ <- data.createTrader(identityA, trader)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

      "delete trader" in {
        data.selectTrader(identity.uuid, trader.uuid).unsafeRunSync() shouldBe None
      }

      "have no trader selected" in {
        data.selectIdentity(identity.uuid).unsafeRunSync() match {
          case None => fail("expected an Identity")
          case Some(updated) => updated.trader shouldBe None
        }
      }
    }

    "another trader deleted" should {
      val traderB = xenocosm.gen.trader.sample.get
      val identityB = identity.copy(trader = Some(traderB))
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identityB, Request(method = Method.DELETE, uri = Uri.uri("/") / ⎈(trader.uuid)))
      val gen = Serial(0L)
      val data = new MemoryDataStore()

      (for {
        _ <- data.createIdentity(identityB)
        _ <- data.createTrader(identityB, trader)
        _ <- data.createTrader(identityB, traderB)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }

      "have same trader selected" in {
        data.selectIdentity(identity.uuid).unsafeRunSync() match {
          case None => fail("expected an Identity")
          case Some(updated) => updated.trader shouldBe Some(traderB)
        }
      }
    }

    "cannot update identity" should {
      val request:AuthedRequest[IO, Identity] = AuthedRequest(identity, Request(method = Method.DELETE, uri = Uri.uri("/") / ⎈(trader.uuid)))
      val gen = Serial(0L)
      val data = new CannotUpdateIdentity()

      (for {
        _ <- data.createIdentity(identity)
      } yield ()).unsafeRunSync()

      val auth = XenocosmAuthentication("test", data)
      val service:AuthedService[Identity, IO] = new TraderAPI(auth, data, gen).service
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 500 status" in {
        checkStatus(response) shouldBe Status.InternalServerError
      }

      "have empty body" in {
        checkBody[String](response) shouldBe None
      }

      "refresh auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }
  }
}
