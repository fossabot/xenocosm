package xenocosm.http
package service

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._

class MultiverseAPISpec extends xenocosm.test.XenocosmSuite with HttpCheck {
  val service:HttpService[IO] = MultiverseAPI.service

  test("GET /v1/multiverse 200") {
    val response:IO[Response[IO]] =
      service
        .run(Request(method = Method.GET, uri = Uri.uri("/v1/multiverse")))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA 200") {
    val response:IO[Response[IO]] =
      service
        .run(Request(method = Method.GET, uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA")))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0 200") {
    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/0,0,0 404") {
    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/0,0,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }


  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0 200") {
    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0 404") {
    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }


  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/-1,0,-1 200") {
    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0/0,-1,0/-1,0,-1")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GET /v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0/0,0,0 404") {
    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA/0,0,0/0,0,0/0,0,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }
}
