package galaxique

import galaxique.data.Point3
import squants.UnitOfMeasure
import squants.space.{Length, Meters}

case object OuterSpace
final case class InnerSpace(loc:Point3)
final case class SubSpace(inner:InnerSpace, loc:Point3)

class SparseSpace3Spec extends xenocosm.test.XenocosmFunSuite {
  import Point3.instances._
  import SparseSpace3.syntax._

  implicit val outerSpaceHasSparseSpace3:SparseSpace3[OuterSpace.type, InnerSpace] =
    new SparseSpace3[OuterSpace.type, InnerSpace] {
      def uom: UnitOfMeasure[Length] = Meters
      def scale: Length = Meters(2)
      def locate(a:OuterSpace.type, loc:Point3):Option[InnerSpace] =
        Some(InnerSpace(loc))
    }

  implicit val innerSpaceHasSparseSpace3:SparseSpace3[InnerSpace, SubSpace] =
    SparseSpace3.fromStandardProof[InnerSpace, SubSpace](
      Meters, Meters(1))(
      SubSpace.apply)(
      inner => Point3.bytes(Meters)(inner.loc))

  test("SparseSpace3.locate") {
    forAll { (loc:Point3) ⇒ OuterSpace.locate(loc) === Some(loc) }
  }

  test("SparseSpace3.nearby") {
    val origin = Point3(Meters(0), Meters(0), Meters(0))
    OuterSpace.nearby(origin, Meters(0)).toList shouldBe List.empty[InnerSpace]
    OuterSpace.nearby(origin, Meters(1)).toList shouldBe List.empty[InnerSpace]
    OuterSpace.nearby(origin, Meters(2)).toList shouldBe List(
      InnerSpace(Point3(Meters(-2),Meters(-2),Meters(-2))),
      InnerSpace(Point3(Meters(-2),Meters(-2),Meters(0))),
      InnerSpace(Point3(Meters(-2),Meters(-2),Meters(2))),
      InnerSpace(Point3(Meters(-2),Meters(0),Meters(-2))),
      InnerSpace(Point3(Meters(-2),Meters(0),Meters(0))),
      InnerSpace(Point3(Meters(-2),Meters(0),Meters(2))),
      InnerSpace(Point3(Meters(-2),Meters(2),Meters(-2))),
      InnerSpace(Point3(Meters(-2),Meters(2),Meters(0))),
      InnerSpace(Point3(Meters(-2),Meters(2),Meters(2))),
      InnerSpace(Point3(Meters(0),Meters(-2),Meters(-2))),
      InnerSpace(Point3(Meters(0),Meters(-2),Meters(0))),
      InnerSpace(Point3(Meters(0),Meters(-2),Meters(2))),
      InnerSpace(Point3(Meters(0),Meters(0),Meters(-2))),
      InnerSpace(Point3(Meters(0),Meters(0),Meters(0))),
      InnerSpace(Point3(Meters(0),Meters(0),Meters(2))),
      InnerSpace(Point3(Meters(0),Meters(2),Meters(-2))),
      InnerSpace(Point3(Meters(0),Meters(2),Meters(0))),
      InnerSpace(Point3(Meters(0),Meters(2),Meters(2))),
      InnerSpace(Point3(Meters(2),Meters(-2),Meters(-2))),
      InnerSpace(Point3(Meters(2),Meters(-2),Meters(0))),
      InnerSpace(Point3(Meters(2),Meters(-2),Meters(2))),
      InnerSpace(Point3(Meters(2),Meters(0),Meters(-2))),
      InnerSpace(Point3(Meters(2),Meters(0),Meters(0))),
      InnerSpace(Point3(Meters(2),Meters(0),Meters(2))),
      InnerSpace(Point3(Meters(2),Meters(2),Meters(-2))),
      InnerSpace(Point3(Meters(2),Meters(2),Meters(0))),
      InnerSpace(Point3(Meters(2),Meters(2),Meters(2))))
  }

  test("SparseSpace3.nearby.standardProof") {
    val origin = Point3(Meters(0), Meters(0), Meters(0))
    val inner = InnerSpace(origin)
    inner.nearby(origin, Meters(0)).toList shouldBe List.empty[SubSpace]
    inner.nearby(origin, Meters(1)).toList shouldBe List(
      SubSpace(inner,Point3(Meters(1), Meters(-1), Meters(1)))
    )
  }
}
