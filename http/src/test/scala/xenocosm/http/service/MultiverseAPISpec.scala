package xenocosm.http
package service

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._
import org.scalacheck.Arbitrary

import xenocosm.data.Trader
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.MemoryDataStore
import xenocosm.data.Trader.instances._

class MultiverseAPISpec extends xenocosm.test.XenocosmSuite with HttpCheck {
  val data = new MemoryDataStore()
  val auth = XenocosmAuthentication("test", data)
  val service:HttpService[IO] = auth.wrap(MultiverseAPI.service)
  val trader:Trader = implicitly[Arbitrary[Trader]].arbitrary.sample.get
  val cookie:Cookie = auth.toCookie(trader)

  test("GET / 403: Missing Cookie") {
    val uri = Uri.uri("/")
    val request:Request[IO] = Request(method = Method.GET, uri = uri)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Forbidden
    checkBody[Json](response).get shouldBe Json.fromString("missing cookies")
  }

  // Add Trader to DataStore
  data.createTrader(trader)

  test("GET / 200") {
    val uri = Uri.uri("/")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /:universeID 200") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /:universeID/:locU 200") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /:universeID/:locU 404") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }


  test("GET /:universeID/:locU/:locG 200") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /:universeID/:locU/:locG 404") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }


  test("GET /:universeID/:locU/:locG/:locS 200") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/-1,0,-1")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /:universeID/:locU/:locG/:locS 404") {
    val uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0/0,0,0")
    val request:Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }
}
