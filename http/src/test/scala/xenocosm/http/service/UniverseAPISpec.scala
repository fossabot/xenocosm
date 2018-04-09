package xenocosm.http
package service

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._

class UniverseAPISpec extends xenocosm.test.XenocosmSuite with HttpCheck {

  test("UniverseAPI.ok") {
    val service:HttpService[IO] = UniverseAPI.service

    val response:IO[Response[IO]] =
      service
        .run(Request(method = Method.GET, uri = Uri.uri("/v1/multiverse/AAAAAAAAAAAAAAAAAAAAAA")))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }
}
