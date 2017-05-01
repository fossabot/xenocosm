package xenocosm
package geometry
package data

import java.nio.ByteBuffer
import cats.PartialOrder
import spire.algebra.MetricSpace
import spire.math.Bounded
import spire.random.Dist
import squants.UnitOfMeasure
import squants.space.Length

import xenocosm.interop.instances._

final case class Point3(x:Length, y:Length, z:Length)

object Point3 {

  val bytes:UnitOfMeasure[Length] ⇒ Point3 ⇒ Array[Byte] = uom ⇒ loc ⇒
    ByteBuffer.
      allocate(8 * 3).
      putDouble(loc.x to uom).
      putDouble(loc.y to uom).
      putDouble(loc.z to uom).
      array()

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
      for {
        x ← implicitly[Dist[Length]]
        y ← implicitly[Dist[Length]]
        z ← implicitly[Dist[Length]]
      } yield Point3(x, y, z)
  }
  object instances extends Instances
}
