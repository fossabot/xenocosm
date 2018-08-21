package xenocosm.http
package rest

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._
import org.scalacheck.Gen
import spire.math.UInt

import xenocosm.data.{ForeignID, Identity}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.MemoryDataStore

class MultiverseAPISpec extends xenocosm.test.XenocosmWordSpec with HttpCheck {
  val genIdentity:Gen[Identity] = for {
    uuid <- Gen.uuid
    ref <- Gen.option(Gen.alphaNumStr.map(ForeignID.apply))
    moves <- Gen.posNum[Int].map(UInt.apply)
  } yield Identity(uuid, ref, moves)

  val identity:Identity = genIdentity.sample.get

  "GET /:universeID" when {
    val data = new MemoryDataStore()
    val auth = XenocosmAuthentication("test", data)
    val cookie:Cookie = auth.toCookie(identity)
    val service:HttpService[IO] = auth.wrap(new MultiverseAPI(auth, data).service)
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA")
    data.createIdentity(identity)

    "unauthenticated" should {
      val request:Request[IO] = Request(method = Method.GET, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }
    }

    "all is well" should {
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }
  }

  "GET /:universeID/:locU" when {
    val data = new MemoryDataStore()
    val auth = XenocosmAuthentication("test", data)
    val cookie:Cookie = auth.toCookie(identity)
    val service:HttpService[IO] = auth.wrap(new MultiverseAPI(auth, data).service)
    data.createIdentity(identity)

    "unauthenticated" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }
    }

    "all is well" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "in intergalactic space" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 404 status" in {
        checkStatus(response) shouldBe Status.NotFound
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }
  }

  "GET /:universeID/:locU/:locG" when {
    val data = new MemoryDataStore()
    val auth = XenocosmAuthentication("test", data)
    val cookie:Cookie = auth.toCookie(identity)
    val service:HttpService[IO] = auth.wrap(new MultiverseAPI(auth, data).service)
    data.createIdentity(identity)

    "unauthenticated" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }
    }

    "all is well" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "in interstellar space" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 404 status" in {
        checkStatus(response) shouldBe Status.NotFound
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }
  }

  "GET /:universeID/:locU/:locG/:locS" when {
    val data = new MemoryDataStore()
    val auth = XenocosmAuthentication("test", data)
    val cookie:Cookie = auth.toCookie(identity)
    val service:HttpService[IO] = auth.wrap(new MultiverseAPI(auth, data).service)
    data.createIdentity(identity)

    "unauthenticated" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/-1,0,-1")
      val request:Request[IO] = Request(method = Method.GET, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 403 status" in {
        checkStatus(response) shouldBe Status.Forbidden
      }
    }

    "all is well" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/-1,0,-1")
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 200 status" in {
        checkStatus(response) shouldBe Status.Ok
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }

    "in interplanetary space" should {
      val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0/0,0,0")
      val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 404 status" in {
        checkStatus(response) shouldBe Status.NotFound
      }

      "respond with JSON body" in {
        checkBody[Json](response).get shouldBe a[Json]
      }
    }
  }
}
