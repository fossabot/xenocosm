package xenocosm
package app
package screen

import java.util.UUID

import squants.space.{LightYears, Parsecs}
import xenocosm.geometry.data.Point3
import xenocosm.universe.data.{Galaxy, Star}

class InterstellarSpaceSpec extends XenocosmAsyncSuite {
  import cats.syntax.show._
  import interop.instances._
  import org.http4s.dsl._
  import org.http4s.headers

  test("InterstellarSpace.nullary.show") {
    InterstellarSpace.apply.show should not be empty
  }

  test("InterstellarSpace.star.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    InterstellarSpace(star).show should not be empty
  }

  test("InterstellarSpace.nullary.entityEncoder") {
    Ok(InterstellarSpace.apply).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

  test("InterstellarSpace.star.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    Ok(InterstellarSpace(star)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

}
