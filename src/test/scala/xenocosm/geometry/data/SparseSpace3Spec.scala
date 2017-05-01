package xenocosm
package geometry
package data

case object OuterSpace
final case class InnerSpace(loc:Point3)

class SparseSpace3Spec extends XenocosmSuite {
  import SparseSpace3.syntax._
  import Point3.instances._

  implicit val outerSpaceHasSparseSpace3 =
    new SparseSpace3[OuterSpace.type, InnerSpace] {
      val locate:OuterSpace.type ⇒ Point3 ⇒ Option[InnerSpace] =
        _ ⇒ loc ⇒ Some(InnerSpace(loc))
    }

  test("SparseSpace3.locate") {
    forAll { (loc:Point3) ⇒ OuterSpace.locate(loc) === Some(loc) }
  }
}
