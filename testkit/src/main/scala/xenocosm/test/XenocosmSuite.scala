package xenocosm
package test

import cats.Eq
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.PropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import spire.random.{Dist, Generator}
import spire.random.rng.BurtleRot2

trait XenocosmSuite extends FunSuite
  with Matchers
  with PropertyChecks
  with Discipline {
  private val generator:Generator = BurtleRot2()
  private def genFromDist[T](dist:Dist[T]):Gen[T] = Gen.const(dist(generator))
  implicit def arbFromDist[T](implicit $ev:Dist[T]):Arbitrary[T] = Arbitrary(genFromDist($ev))
  implicit def eqCogenFromHashCode[T:Eq]:Cogen[T] = Cogen[Int].contramap(_.hashCode)
}
