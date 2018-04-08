package xenocosm.http
package service

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._

class GalaxyAPISpec extends xenocosm.test.XenocosmSuite with HttpCheck {

  test("GalaxyAPI.ok") {
    val service:HttpService[IO] = GalaxyAPI.service

    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/-1,-1,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }

  test("GalaxyAPI.not-found") {
    val service:HttpService[IO] = GalaxyAPI.service

    val response:IO[Response[IO]] =
      service
        .run(Request(
          method = Method.GET,
          uri = Uri.uri("/AAAAAAAAAAAAAAAAAAAAAA/0,0,0")
        ))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.NotFound
    checkBody[Json](response).get shouldBe a[Json]
  }
}
