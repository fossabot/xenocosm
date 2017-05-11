package xenocosm
package app
package screen

import java.util.UUID

class UniverseSpec extends XenocosmAsyncSuite {
  import cats.syntax.show._
  import interop.instances._
  import org.http4s.dsl._
  import org.http4s.headers

  test("Universe.universe.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    Universe(universe).show should not be empty
  }

  test("Universe.universe.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    Ok(Universe(universe)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

}
