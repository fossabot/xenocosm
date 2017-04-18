import cats.Eq
import org.scalacheck.{Arbitrary, Cogen, Gen}
import spire.random.rng.BurtleRot2
import spire.random.{Dist, Generator}

package object xenocosm {
  private val generator:Generator = BurtleRot2()
  private def genFromDist[T](dist:Dist[T]):Gen[T] = Gen.const(dist(generator))
  implicit def arbFromDist[T](implicit $ev:Dist[T]):Arbitrary[T] = Arbitrary(genFromDist($ev))
  implicit def eqCogenFromHashCode[T:Eq]:Cogen[T] = Cogen[Int].contramap(_.hashCode)
}
