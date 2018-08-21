package xenocosm.http
package middleware

import cats.effect.IO
import io.circe.Json
import io.circe.syntax._
import org.http4s.{AuthedService, Cookie, HttpService, Method, Request, Response, Status, Uri}
import org.http4s.circe._
import org.http4s.dsl.io._
import org.reactormonk.{CryptoBits, PrivateKey}
import org.scalacheck.Gen
import spire.math.UInt

import xenocosm.data.{ForeignID, Identity}
import xenocosm.http.services.MemoryDataStore

class XenocosmAuthenticationSpec extends xenocosm.test.XenocosmFunSuite with HttpCheck {
  import xenocosm.json.identity._

  val genIdentity:Gen[Identity] = for {
    uuid <- Gen.uuid
    ref <- Gen.option(Gen.alphaNumStr.map(ForeignID.apply))
    moves <- Gen.posNum[Int].map(UInt.apply)
  } yield Identity(uuid, ref, moves)

  val identityA:Identity = genIdentity.sample.get
  val crypto: CryptoBits = CryptoBits(PrivateKey(scala.io.Codec.toUTF8("test")))
  val data = new MemoryDataStore()
  val auth = XenocosmAuthentication("test", data)
  val uri:Uri = Uri.uri("/")

  val service: HttpService[IO] = auth.wrap(AuthedService[Identity, IO] {
    case GET -> Root as identityB => Ok(identityB.asJson)
  })

  test("handle no cookies provided") {
    val request: Request[IO] = Request(method = Method.GET, uri = uri)
    val response: IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus(response) shouldBe Status.Forbidden
    checkBody[Json](response).get shouldBe Json.fromString("missing cookies")
  }

  test("handle no auth cookie provided") {
    val cookie = Cookie("foo", "bar")
    val request: Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response: IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus(response) shouldBe Status.Forbidden
    checkBody[Json](response).get shouldBe Json.fromString("missing auth cookie")
  }

  test("handle auth cookie with bad signature") {
    val cookie = Cookie(XenocosmAuthentication.cookieName, "bar")
    val request: Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response: IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus(response) shouldBe Status.Forbidden
    checkBody[Json](response).get shouldBe Json.fromString("invalid cookie signature")
  }

  test("handle auth cookie with bad UUID") {
    val cookie = Cookie(XenocosmAuthentication.cookieName, crypto.signToken("foo", "0"))
    val request: Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response: IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus(response) shouldBe Status.Forbidden
    checkBody[Json](response).get shouldBe Json.fromString("invalid UUID")
  }

  test("handle Identity cannot be found by UUID") {
    val cookie = auth.toCookie(identityA)
    val request: Request[IO] = Request(method = Method.GET, uri = uri).addCookie(cookie)
    val response: IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

    checkStatus(response) shouldBe Status.Forbidden
    checkBody[Json](response).get shouldBe Json.fromString("Identity not found")
  }
}
