package xenocosm
package universe
package data

import cats.kernel.laws.OrderLaws
import spire.random.Dist
import xenocosm.geometry.data.Point3

class StarSpec extends XenocosmSuite {
  import xenocosm.geometry.data.Point3.instances._
  import Universe.instances._
  import Star.instances._

  implicit val dist:Dist[Star] =
    for {
      universe ← implicitly[Dist[Universe]]
      loc1 ← implicitly[Dist[Point3]]
      galaxy ← Galaxy.dist(universe, loc1)
      loc2 ← implicitly[Dist[Point3]]
      star ← Star.dist(galaxy, loc2)
    } yield star

  checkAll("Eq[Star]", OrderLaws[Star].eqv)
}
