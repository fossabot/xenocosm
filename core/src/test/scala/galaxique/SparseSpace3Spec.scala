package galaxique

import galaxique.data.Point3

case object OuterSpace
final case class InnerSpace(loc:Point3)

class SparseSpace3Spec extends xenocosm.test.XenocosmSuite {
  import Point3.instances._
  import SparseSpace3.syntax._

  implicit val outerSpaceHasSparseSpace3:SparseSpace3[OuterSpace.type, InnerSpace] =
    new SparseSpace3[OuterSpace.type, InnerSpace] {
      def locate(a:OuterSpace.type, loc:Point3):Option[InnerSpace] =
        Some(InnerSpace(loc))
    }

  test("SparseSpace3.locate") {
    forAll { (loc:Point3) ⇒ OuterSpace.locate(loc) === Some(loc) }
  }
}
