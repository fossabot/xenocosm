package xenocosm
package geometry
package data

import cats.Eq
import spire.algebra.MetricSpace
import spire.math.Bounded
import spire.random.Dist
import squants.space.Length
import xenocosm.instances.interop._

final case class Point3(x:Length, y:Length, z:Length)

object Point3 {

  def wholePointsInCube(step:Length, origin:Point3):Iterator[Point3] =
    for {
      x ← Bounded(origin.x - step, origin.x + step, 0).iterator(step)
      y ← Bounded(origin.y - step, origin.y + step, 0).iterator(step)
      z ← Bounded(origin.z - step, origin.z + step, 0).iterator(step)
    } yield Point3(x.rint, y.rint, z.rint)

  trait Instances {
    implicit val point3HasEq:Eq[Point3] = Eq.fromUniversalEquals[Point3]

    implicit val point3HasMetricSpace:MetricSpace[Point3, Length] =
      new MetricSpace[Point3, Length] {
        def distance(v:Point3, w:Point3):Length =
          ((v.x - w.x).squared + (v.y - w.y).squared + (v.z - w.z).squared).squareRoot
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
