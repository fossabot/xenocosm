package xenocosm
package geometry
package data

import cats.implicits._
import squants.UnitOfMeasure
import squants.space.Length

trait SparseSpace3[A, B] {
  val locate:A ⇒ Point3 ⇒ Option[B]
}

object SparseSpace3 {

  def instance[A, B](uom:UnitOfMeasure[Length], f:(A, Point3) ⇒ B)(g:A ⇒ Array[Byte]):SparseSpace3[A, B] = {
    val add:(Array[Byte], Array[Byte]) ⇒ Array[Byte] = { case (x, y) ⇒ x ++ y }
    val bytes = (g split Point3.bytes(uom)) map add.tupled
    val proof:Array[Byte] ⇒ Option[Byte] = _.headOption.filter(_ % 16 == 0)
    val composed = proof compose Digest.sha256 compose bytes

    new SparseSpace3[A, B] {
      val locate:A ⇒ Point3 ⇒ Option[B] = a ⇒ loc ⇒ composed((a, loc)) map (_ ⇒ f(a, loc))
    }
  }

  trait Syntax {
    implicit class SparseSpace3Ops[A, B](underlying:A)(implicit ev:SparseSpace3[A, B]) {
      def locate(loc:Point3):Option[B] = ev.locate(underlying)(loc)
    }
  }
  object syntax extends Syntax
}
