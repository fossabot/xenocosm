package xenocosm.test

import cats.Eq
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.scalatest.Matchers
import org.scalatest.prop.PropertyChecks
import spire.random.Generator
import spire.random.rng.BurtleRot2

trait XenocosmSuiteBase extends Matchers with PropertyChecks {
  implicit def eqCogenFromHashCode[T:Eq]:Cogen[T] = Cogen[Int].contramap(_.hashCode)

  def genInts:Gen[Array[Int]] =
    for {
      a <- Arbitrary.arbInt.arbitrary
      b <- Arbitrary.arbInt.arbitrary
      c <- Arbitrary.arbInt.arbitrary
      d <- Arbitrary.arbInt.arbitrary
    } yield Array(a, b, c, d)

  def spireRNG:Generator = BurtleRot2.fromSeed(genInts.sample.get)
}
