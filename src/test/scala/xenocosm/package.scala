import java.util.UUID

import cats.Eq
import org.scalacheck.{Arbitrary, Cogen, Gen}
import spire.random.rng.BurtleRot2
import spire.random.{Dist, Generator}

import xenocosm.geometry.data.Point3
import xenocosm.interop.instances._
import xenocosm.universe.data.{Galaxy, Star, Universe}
import Point3.instances._

package object xenocosm {
  private val generator:Generator = BurtleRot2()
  private def genFromDist[T](dist:Dist[T]):Gen[T] = Gen.const(dist(generator))
  implicit def arbFromDist[T](implicit $ev:Dist[T]):Arbitrary[T] = Arbitrary(genFromDist($ev))
  implicit def eqCogenFromHashCode[T:Eq]:Cogen[T] = Cogen[Int].contramap(_.hashCode)

  implicit val distUniverse:Dist[Universe] = implicitly[Dist[UUID]].map(Universe.apply)
  implicit val distGalaxy:Dist[Galaxy] =
    for {
      universe ← distUniverse
      loc ← implicitly[Dist[Point3]]
    } yield Galaxy(universe, loc)

  implicit val distStar:Dist[Star] =
    for {
      galaxy ← distGalaxy
      loc ← implicitly[Dist[Point3]]
    } yield Star(galaxy, loc)
}
