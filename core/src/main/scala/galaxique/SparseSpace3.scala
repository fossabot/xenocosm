package galaxique

import java.security.MessageDigest
import cats.implicits._
import squants.UnitOfMeasure
import squants.space.Length
import galaxique.data.Point3
import interop.length._

trait SparseSpace3[A, B] {
  def uom:UnitOfMeasure[Length]
  def scale:Length
  def locate(a:A, loc:Point3):Option[B]
  def nearby(a:A, origin:Point3, range:Length):Iterator[B] =
    if (scale === uom(0) || range < scale) {
      Iterator.empty
    } else {
      val steps = range / scale
      Point3
        .wholePointsInCube(scale * steps * 2, scale, origin)
        .flatMap(loc => locate(a, loc.in(uom)))
    }
}

object SparseSpace3 {
  private val md:MessageDigest = MessageDigest.getInstance("SHA-256")

  private val standardProof:Array[Byte] => Option[Byte] =
    bytes => md.digest(bytes).headOption.filter(_ % 16 === 0)

  def fromStandardProof[A, B](lenUOM:UnitOfMeasure[Length], lenScale:Length)(f:(A, Point3) ⇒ B)(g:A ⇒ Array[Byte]):SparseSpace3[A, B] =
    new SparseSpace3[A, B] {
      def uom:UnitOfMeasure[Length] = lenUOM
      def scale:Length = lenScale
      def locate(a:A, loc:Point3):Option[B] = standardProof(g(a) ++ Point3.bytes(uom)(loc)).map(_ => f(a, loc))
    }

  trait Syntax {
    implicit class SparseSpace3Ops[A, B](underlying:A)(implicit ev:SparseSpace3[A, B]) {
      def locate(loc:Point3):Option[B] = ev.locate(underlying, loc)
      def nearby(origin:Point3, range:Length):Iterator[B] = ev.nearby(underlying, origin, range)
    }
  }
  object syntax extends Syntax
}
