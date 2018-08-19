package xenocosm.http
package middleware

import cats.effect.IO
import org.http4s.{HttpService, Method, Request, Response, Uri}
import org.http4s.dsl.io._
import org.http4s.util.CaseInsensitiveString

class ServerHeaderSpec extends xenocosm.test.XenocosmFunSuite {

  def service200:HttpService[IO] = ServerHeader.wrap(HttpService[IO] { case GET -> Root => Ok() })
  def service400:HttpService[IO] = ServerHeader.wrap(HttpService[IO] { case GET -> Root => BadRequest() })
  def service500:HttpService[IO] = ServerHeader.wrap(HttpService[IO] { case GET -> Root => InternalServerError() })
  def request(service:HttpService[IO], req:Request[IO]):Response[IO] =
    service.orNotFound.run(req).unsafeRunSync()
  def serverHeader(res:Response[IO]):Option[String] =
    res.headers.get(CaseInsensitiveString("Server")).map(_.value)

  test("Server header has Xenocosm version") {
    val req:Request[IO] = Request(method = Method.GET, uri = Uri.uri("/"))
    val response200 = request(service200, req)
    val response400 = request(service400, req)
    val response500 = request(service500, req)

    serverHeader(response200) shouldBe Some(s"xenocosm/${xenocosm.BuildInfo.version}")
    serverHeader(response400) shouldBe Some(s"xenocosm/${xenocosm.BuildInfo.version}")
    serverHeader(response500) shouldBe Some(s"xenocosm/${xenocosm.BuildInfo.version}")
  }
}
