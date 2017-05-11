package xenocosm
package app
package screen

class MultiverseSpec extends XenocosmAsyncSuite {
  import cats.syntax.show._
  import interop.instances._
  import org.http4s.dsl._
  import org.http4s.headers

  test("Multiverse.nullary.show") {
    Multiverse.apply.show should not be empty
  }

  test("Multiverse.nullary.entityEncoder") {
    Ok(Multiverse.apply).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

}
