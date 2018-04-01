package galaxique

import java.security.MessageDigest
import squants.UnitOfMeasure
import squants.space.Length
import galaxique.data.Point3

trait SparseSpace3[A, B] {
  def locate(a:A, loc:Point3):Option[B]
}

object SparseSpace3 {
  import cats.implicits._

  private def sha256(bytes:Array[Byte]):Array[Byte] =
    MessageDigest.getInstance("SHA-256").digest(bytes)

  private def proof(bytes:Array[Byte]):Option[Byte] =
    sha256(bytes).headOption.filter(_ % 16 === 0)

  def instance[A, B](uom:UnitOfMeasure[Length], f:(A, Point3) ⇒ B)(g:A ⇒ Array[Byte]):SparseSpace3[A, B] = {
    val bytes:A => Point3 => Array[Byte] = a => loc => g(a) ++ Point3.bytes(uom)(loc)

    new SparseSpace3[A, B] {
      def locate(a:A, loc:Point3):Option[B] = proof(bytes(a)(loc)).map(_ => f(a, loc))
    }
  }

  trait Syntax {
    implicit class SparseSpace3Ops[A, B](underlying:A)(implicit ev:SparseSpace3[A, B]) {
      def locate(loc:Point3):Option[B] = ev.locate(underlying, loc)
    }
  }
  object syntax extends Syntax
}
