package galaxique
package data

import java.nio.ByteBuffer
import cats.PartialOrder
import spire.algebra.MetricSpace
import spire.math.Bounded
import spire.random.Dist
import squants.UnitOfMeasure
import squants.space.{Length, LightYears, Meters}

final case class Point3(x:Length, y:Length, z:Length)

object Point3 {
  import interop.length._

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
      x ← Bounded(origin.x - (side / 2), origin.x + (side / 2), 0).iterator(step)
      y ← Bounded(origin.y - (side / 2), origin.y + (side / 2), 0).iterator(step)
      z ← Bounded(origin.z - (side / 2), origin.z + (side / 2), 0).iterator(step)
    } yield Point3(x.rint, y.rint, z.rint)

  trait Instances {

    implicit val point3HasMetricSpace:MetricSpace[Point3, Length] =
      new MetricSpace[Point3, Length] {
        def distance(v:Point3, w:Point3):Length =
          ((v.x - w.x).squared + (v.y - w.y).squared + (v.z - w.z).squared).squareRoot
      }

    implicit val point3HasEq:PartialOrder[Point3] =
      PartialOrder.from {
        (a:Point3, b:Point3) ⇒ point3HasMetricSpace.distance(a, b).to(a.x.unit)
      }

    implicit val point3HasDist:Dist[Point3] =
      Dist.array[Int](3, 3).map(xs => Point3(LightYears(xs(0)), LightYears(xs(1)), LightYears(xs(2))))
  }
  object instances extends Instances
}
