package xenocosm.http
package rest

import cats.effect.IO
import org.http4s._
import org.http4s.headers.{`WWW-Authenticate`, Authorization, Location}

import xenocosm.data.{ForeignID, Identity}
import xenocosm.http.middleware.XenocosmAuthentication
import xenocosm.http.services.MemoryDataStore

class AuthAPISpec extends xenocosm.test.XenocosmWordSpec with HttpCheck {

  private class CannotCreateIdentity extends MemoryDataStore {
    override def createIdentity(identity:Identity):IO[Unit] =
      IO.raiseError(new Exception("boom"))
  }

  private class CannotFindIdentity extends MemoryDataStore {
    override def selectIdentity(ref:ForeignID):IO[Option[Identity]] =
      IO.raiseError(new Exception("boom"))
  }

  val identity:Identity = xenocosm.gen.identity.sample.get

  "POST /" when {
    val uri = Uri.uri("/")

    "missing Basic Auth credentials" should {
      val data = new MemoryDataStore()
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

      "not set auth token" in {
        checkAuthToken(response) shouldBe empty
      }
    }

    "provided with existing Basic Auth credentials" should {
      val data = new MemoryDataStore()
      data.createIdentity(identity.copy(ref = Some(ForeignID("foo")))).unsafeRunSync()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val basic:Authorization = Authorization(BasicCredentials("foo", ""))
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri, headers = Headers.apply(basic))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 303 status" in {
        checkStatus(response) shouldBe Status.SeeOther
      }

      "respond with Location in header" in {
        val uri = response
          .unsafeRunSync()
          .headers
          .get(Location)
          .map(_.uri)

        if (identity.trader.isDefined) {
          uri shouldBe Some(Uri.uri("/trader") / ⎈(identity.trader.get.uuid))
        } else {
          uri shouldBe Some(Uri.uri("/trader"))
        }
      }

      "set auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "provided with unrecognized Basic Auth credentials" should {
      val data = new MemoryDataStore()
      data.createIdentity(identity.copy(ref = Some(ForeignID("bar")))).unsafeRunSync()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val basic:Authorization = Authorization(BasicCredentials("foo", ""))
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri, headers = Headers.apply(basic))
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "create new identity" in {
        response.unsafeRunSync()
        val ref = ForeignID("foo")
        val identityB = data.selectIdentity(ref).unsafeRunSync()
        identityB shouldBe defined
        identityB.get.ref shouldBe Some(ref)
      }

      "respond with 303 status" in {
        checkStatus(response) shouldBe Status.SeeOther
      }

      "respond with Location in header" in {
        val uri = response
          .unsafeRunSync()
          .headers
          .get(Location)
          .map(_.uri)

        uri shouldBe Some(Uri.uri("/trader"))
      }

      "set auth token" in {
        checkAuthToken(response) shouldBe defined
      }
    }

    "cannot select identity" should {
      val data = new CannotFindIdentity()
      data.createIdentity(identity.copy(ref = Some(ForeignID("bar")))).unsafeRunSync()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val basic:Authorization = Authorization(BasicCredentials("foo", ""))
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri, headers = Headers.apply(basic))
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

      "not set auth token" in {
        checkAuthToken(response) shouldBe empty
      }
    }

    "cannot create identity" should {
      val data = new CannotCreateIdentity()
      val auth = XenocosmAuthentication("test", data)
      val service:HttpService[IO] = new AuthAPI(auth, data).service
      val basic:Authorization = Authorization(BasicCredentials("foo", ""))
      val request:Request[IO] = Request.apply(method = Method.POST, uri = uri, headers = Headers.apply(basic))
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

      "not set auth token" in {
        checkAuthToken(response) shouldBe empty
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

      "not set auth token" in {
        checkAuthToken(response) shouldBe empty
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
        checkAuthToken(response) shouldBe defined
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

      "set expired auth token" in {
        checkAuthToken(response) shouldBe defined
        checkAuthToken(response).flatMap(_.expires) shouldBe Some(HttpDate.Epoch)
      }
    }

    "authenticated" should {
      val cookie:Cookie = auth.toCookie(identity)
      val request:Request[IO] = Request(method = Method.DELETE, uri = uri).addCookie(cookie)
      val response:IO[Response[IO]] = service.run(request).getOrElse(Response.notFound)

      "respond with 204 status" in {
        checkStatus(response) shouldBe Status.NoContent
      }

      "set expired auth token" in {
        checkAuthToken(response) shouldBe defined
        checkAuthToken(response).flatMap(_.expires) shouldBe Some(HttpDate.Epoch)
      }
    }
  }
}
