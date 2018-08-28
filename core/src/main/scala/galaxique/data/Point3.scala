package galaxique
package data

import java.nio.ByteBuffer
import cats.kernel.Eq
import spire.algebra.MetricSpace
import spire.math.Bounded
import squants.UnitOfMeasure
import squants.space.{Length, Meters}

final case class Point3(x:Length, y:Length, z:Length) {
  def in(uom:UnitOfMeasure[Length]):Point3 =
    Point3(x.in(uom), y.in(uom), z.in(uom))
}

object Point3 {
  import interop.squants.instances._
  import spire.syntax.metricSpace._

  val zero:Point3 = Point3(Meters(0), Meters(0), Meters(0))

  private[galaxique] val bytes:UnitOfMeasure[Length] ⇒ Point3 ⇒ Array[Byte] = uom ⇒ loc ⇒
    ByteBuffer
      .allocate(24)
      .putDouble(loc.x to uom)
      .putDouble(loc.y to uom)
      .putDouble(loc.z to uom)
      .array()

  def wholePointsInCube(side:Length, step:Length, origin:Point3):Iterator[Point3] =
    for {
      x <- Bounded(origin.x - (side / 2), origin.x + (side / 2), 0).iterator(step)
      y <- Bounded(origin.y - (side / 2), origin.y + (side / 2), 0).iterator(step)
      z <- Bounded(origin.z - (side / 2), origin.z + (side / 2), 0).iterator(step)
    } yield Point3(x.rint, y.rint, z.rint)

  trait Instances {

    implicit val point3HasMetricSpace:MetricSpace[Point3, Length] =
      (v:Point3, w:Point3) =>
        ((v.x - w.x).squared + (v.y - w.y).squared + (v.z - w.z).squared).squareRoot

    implicit val point3HasEq:Eq[Point3] = Eq.fromUniversalEquals[Point3]

  }
  object instances extends Instances
  import instances._

  def distance(a:Option[Point3], b:Option[Point3]):Length =
    (a, b) match {
      case (Some(locA), Some(locB)) => locA distance locB
      case (None, Some(locB)) => Point3.zero distance locB
      case (Some(locA), None) => Point3.zero distance locA
      case _ => Point3.zero.x
    }
}
