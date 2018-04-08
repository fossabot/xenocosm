package xenocosm
package test

import cats.Eq
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.PropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import spire.random.Dist
import spire.random.rng.BurtleRot2

trait XenocosmSuite extends FunSuite
  with Matchers
  with PropertyChecks
  with Discipline {
  private def genFromDist[T](dist:Dist[T]):Gen[T] =
    for {
      a <- Gen.chooseNum(Int.MinValue, Int.MaxValue)
      b <- Gen.chooseNum(Int.MinValue, Int.MaxValue)
      c <- Gen.chooseNum(Int.MinValue, Int.MaxValue)
      d <- Gen.chooseNum(Int.MinValue, Int.MaxValue)
    } yield dist.apply(BurtleRot2.fromSeed(Array(a, b, c, d)))
  implicit def arbFromDist[T](implicit $ev:Dist[T]):Arbitrary[T] = Arbitrary(genFromDist($ev))
  implicit def eqCogenFromHashCode[T:Eq]:Cogen[T] = Cogen[Int].contramap(_.hashCode)
}
