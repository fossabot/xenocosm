package xenocosm
package geometry
package data

import java.nio.ByteBuffer
import java.security.MessageDigest

import cats.PartialOrder
import spire.algebra.MetricSpace
import spire.math.Bounded
import spire.random.Dist
import squants.UnitOfMeasure
import squants.space.Length
import xenocosm.instances.interop._

final case class Point3(x:Length, y:Length, z:Length) {
  private def bytes(uom:UnitOfMeasure[Length]):Array[Byte] =
    ByteBuffer.
      allocate(24).
      putDouble(x to uom).
      putDouble(y to uom).
      putDouble(z to uom).
      array()

  def digest(uom:UnitOfMeasure[Length]):Array[Byte] = MessageDigest.getInstance("MD5").digest(bytes(uom))
}

object Point3 {

  def wholePointsInCube(step:Length, origin:Point3):Iterator[Point3] =
    for {
      x ← Bounded(origin.x - step, origin.x + step, 0).iterator(step)
      y ← Bounded(origin.y - step, origin.y + step, 0).iterator(step)
      z ← Bounded(origin.z - step, origin.z + step, 0).iterator(step)
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
