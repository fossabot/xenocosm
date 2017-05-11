package xenocosm
package app
package screen

import java.util.UUID

import squants.space.{Kilometers, LightYears, Parsecs}
import xenocosm.geometry.data.Point3
import xenocosm.universe.data.{Universe ⇒ _, _}

class InterplanetarySpaceSpec extends XenocosmAsyncSuite {
  import cats.syntax.show._
  import interop.instances._
  import org.http4s.dsl._
  import org.http4s.headers

  test("InterplanetarySpace.nullary.show") {
    InterplanetarySpace.apply.show should not be empty
  }

  test("InterplanetarySpace.planet.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    val planet = Planet(star, Point3(Kilometers(0), Kilometers(0), Kilometers(0)))
    InterplanetarySpace(planet).show should not be empty
  }

  test("InterplanetarySpace.dwarfPlanet.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    val dwarfPlanet = DwarfPlanet(star, Point3(Kilometers(0), Kilometers(0), Kilometers(0)))
    InterplanetarySpace(dwarfPlanet).show should not be empty
  }

  test("InterplanetarySpace.smallBody.show") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    val smallBody = SmallBody(star, Point3(Kilometers(0), Kilometers(0), Kilometers(0)))
    InterplanetarySpace(smallBody).show should not be empty
  }

  test("InterplanetarySpace.nullary.entityEncoder") {
    Ok(InterplanetarySpace.apply).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

  test("InterplanetarySpace.planet.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    val planet = Planet(star, Point3(Kilometers(0), Kilometers(0), Kilometers(0)))
    Ok(InterplanetarySpace(planet)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

  test("InterplanetarySpace.dwarfPlanet.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    val dwarfPlanet = DwarfPlanet(star, Point3(Kilometers(0), Kilometers(0), Kilometers(0)))
    Ok(InterplanetarySpace(dwarfPlanet)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

  test("InterplanetarySpace.smallBody.entityEncoder") {
    val universe = xenocosm.universe.data.Universe(UUID.randomUUID())
    val galaxy = Galaxy(universe, Point3(Parsecs(0), Parsecs(0), Parsecs(0)))
    val star = Star(galaxy, Point3(LightYears(0), LightYears(0), LightYears(0)))
    val smallBody = SmallBody(star, Point3(Kilometers(0), Kilometers(0), Kilometers(0)))
    Ok(InterplanetarySpace(smallBody)).map({ res ⇒
      res.headers.get(headers.`Content-Type`.name).map(_.value) should be (Some("text/plain; charset=UTF-8"))
      res.headers.get(headers.`Content-Length`.name).map(_.value.toLong) should not be empty
    }).unsafeRunAsyncFuture()
  }

}
