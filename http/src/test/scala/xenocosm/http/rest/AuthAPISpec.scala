package xenocosm.http
package rest

import cats.effect.IO
import org.http4s._
import org.http4s.headers.{`WWW-Authenticate`, Authorization, Location}
import org.scalacheck.Gen
import spire.math.UInt

import xenocosm.data.{ForeignID, Identity}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.MemoryDataStore

class AuthAPISpec extends xenocosm.test.XenocosmWordSpec with HttpCheck {
  val genIdentity:Gen[Identity] = for {
    uuid <- Gen.uuid
    ref <- Gen.option(Gen.alphaNumStr.map(ForeignID.apply))
    moves <- Gen.posNum[Int].map(UInt.apply)
  } yield Identity(uuid, ref, moves)

  val identity:Identity = genIdentity.sample.get.copy(ref = Some(ForeignID("foo")))

  "POST /" when {
    val uri = Uri.uri("/")

    "missing Basic Auth credentials" should {
      val data = new MemoryDataStore()
      data.createIdentity(identity).unsafeRunSync()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 401 status" in {
        checkStatus(response) shouldBe Status.Unauthorized
      }

      "respond with Challenge in header" in {
        response
          .unsafeRunSync()
          .headers
          .get(`WWW-Authenticate`)
          .map(_.values.head) shouldBe Some(Challenge("Basic", "Xenocosm", Map.empty[String, String]))
      }
    }

    "provided with existing Basic Auth credentials" should {
      val data = new MemoryDataStore()
      data.createIdentity(identity).unsafeRunSync()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val basic:Authorization = Authorization(BasicCredentials("foo", ""))
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri, headers = Headers.apply(basic))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 303 status" in {
        checkStatus(response) shouldBe Status.SeeOther
      }

      "respond with Location in header" in {
        response
          .unsafeRunSync()
          .headers
          .get(Location)
          .map(_.uri) shouldBe Some(Uri.uri("/"))
      }
    }

    "provided with unrecognized Basic Auth credentials" should {
      val data = new MemoryDataStore()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val basic:Authorization = Authorization(BasicCredentials("foo", ""))
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri, headers = Headers.apply(basic))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "create new identity" in {
        response.unsafeRunSync()
        val ref = ForeignID("foo")
        val identity = data.selectIdentity(ref).unsafeRunSync()
        identity shouldBe defined
        identity.get.ref shouldBe Some(ref)
      }

      "respond with 303 status" in {
        checkStatus(response) shouldBe Status.SeeOther
      }

      "respond with Location in header" in {
        response
          .unsafeRunSync()
          .headers
          .get(Location)
          .map(_.uri) shouldBe Some(Uri.uri("/"))
      }
    }
  }

  "HEAD /" when {
    val data = new MemoryDataStore()
    val auth = XenocosmAuthentication("test", data)
    val service:HttpService[IO] = new AuthAPI(auth, data).service
    val uri = Uri.uri("/")

    "unauthenticated" should {
      val request:Request[IO] = Request(method = Method.HEAD, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 401 status" in {
        checkStatus(response) shouldBe Status.Unauthorized
      }

      "respond with Challenge in header" in {
        response
          .unsafeRunSync()
          .headers
          .get(`WWW-Authenticate`)
          .map(_.values.head) shouldBe Some(Challenge("Basic", "Xenocosm", Map.empty[String, String]))
      }
    }

    "authenticated" should {
      data.createIdentity(identity).unsafeRunSync()
      val cookie:Cookie = auth.toCookie(identity)
      val request:Request[IO] = Request(method = Method.HEAD, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }

      "refresh auth token" in {
        response
          .unsafeRunSync()
          .cookies
          .find(_.name === XenocosmAuthentication.cookieName)
          .map(_.name) shouldBe Some(XenocosmAuthentication.cookieName)
      }
    }
  }

  "DELETE /" when {
    val data = new MemoryDataStore()
    val auth = XenocosmAuthentication("test", data)
    val service:HttpService[IO] = new AuthAPI(auth, data).service
    val uri = Uri.uri("/")

    "unauthenticated" should {
      val request:Request[IO] = Request(method = Method.DELETE, uri = uri)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }
    }

    "authenticated" should {
      val cookie:Cookie = auth.toCookie(identity)
      val request:Request[IO] = Request(method = Method.DELETE, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }

      "not return auth token" in {
        response
          .unsafeRunSync()
          .cookies
          .find(_.name === XenocosmAuthentication.cookieName) shouldBe None
      }
    }
  }
}
