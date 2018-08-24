package galaxique

import java.security.MessageDigest
import cats.implicits._
import squants.UnitOfMeasure
import squants.space.Length
import galaxique.data.Point3

trait SparseSpace3[A, B] { self =>
  def uom:UnitOfMeasure[Length]
  def scale:Length
  def locate(a:A, loc:Point3):Option[B]
  def nearby(a:A, origin:Point3, range:Length):Iterator[B] =
    SparseSpace3.nearby(self, a, origin, range)
}

object SparseSpace3 {
  import interop.squants.instances._

  private val md:MessageDigest = MessageDigest.getInstance("SHA-256")

  private val standardProof:Array[Byte] => Option[Byte] =
    bytes => md.digest(bytes).headOption.filter(_ % 16 === 0)

  def fromStandardProof[A, B](lenUOM:UnitOfMeasure[Length], lenScale:Length)(f:(A, Point3) ⇒ B)(g:A ⇒ Array[Byte]):SparseSpace3[A, B] =
    new SparseSpace3[A, B] {
      def uom:UnitOfMeasure[Length] = lenUOM
      def scale:Length = lenScale
      def locate(a:A, loc:Point3):Option[B] = standardProof(g(a) ++ Point3.bytes(uom)(loc)).map(_ => f(a, loc))
    }

  def nearby[A, B](ss3:SparseSpace3[A, B], a:A, origin:Point3, range:Length):Iterator[B] =
    if (ss3.scale === ss3.uom(0) || range < ss3.scale) {
      Iterator.empty
    } else {
      val steps = range / ss3.scale
      Point3
        .wholePointsInCube(ss3.scale * steps * 2, ss3.scale, origin)
        .flatMap(loc => ss3.locate(a, loc.in(ss3.uom)))
    }

  trait Syntax {
    implicit class SparseSpace3Ops[A, B](underlying:A)(implicit ev:SparseSpace3[A, B]) {
      def locate(loc:Point3):Option[B] = ev.locate(underlying, loc)
      def nearby(origin:Point3, range:Length):Iterator[B] = ev.nearby(underlying, origin, range)
      def nearest(origin:Point3, range:Length):Option[B] =
        nearby(origin, range).take(1).toList.headOption
    }
  }
  object syntax extends Syntax
}
