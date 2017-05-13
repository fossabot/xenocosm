package xenocosm
package app
package screen

import java.util.UUID
import squants.space.Parsecs

import xenocosm.chapbook.GalacticForge
import xenocosm.geometry.data.Point3
import xenocosm.universe.data.Galaxy

class IntergalacticSpaceSpec extends XenocosmAsyncSuite {
  import cats.syntax.show._
  import interop.instances._
  import org.http4s.dsl._
  import org.http4s.headers

  test("IntergalacticSpace.nullary.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val loc = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
    val stanza = GalacticForge.fromIntergalacticSpace((universe, loc))
    IntergalacticSpace.show(stanza) should not be empty
  }

  test("IntergalacticSpace.galaxy.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    IntergalacticSpace(galaxy).show should not be empty
  }

  test("IntergalacticSpace.nullary.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val loc = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
    val stanza = GalacticForge.fromIntergalacticSpace((universe, loc))
    Ok(IntergalacticSpace(stanza)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

  test("IntergalacticSpace.galaxy.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    Ok(IntergalacticSpace(galaxy)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

}
