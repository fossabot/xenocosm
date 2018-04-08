package xenocosm.http
package service

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe._

class MultiverseAPISpec extends xenocosm.test.XenocosmSuite with HttpCheck {

  test("MultiverseAPI.ok") {
    val service:HttpService[IO] = MultiverseAPI.service

    val response:IO[Response[IO]] =
      service
        .run(Request(method = Method.GET, uri = Uri.uri("/")))
        .getOrElse(Response.notFound)

    checkStatus[Json](response) shouldBe Status.Ok
    checkBody[Json](response).get shouldBe a[Json]
  }
}
