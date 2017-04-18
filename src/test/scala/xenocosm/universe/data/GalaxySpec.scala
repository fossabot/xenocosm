package xenocosm
package universe
package data

import cats.kernel.laws.OrderLaws
import spire.random.Dist
import xenocosm.geometry.data.Point3

class GalaxySpec extends XenocosmSuite {
  import xenocosm.geometry.data.Point3.instances._
  import Galaxy.instances._
  import Universe.instances._

  implicit val dist:Dist[Galaxy] =
    for {
      universe ← implicitly[Dist[Universe]]
      loc ← implicitly[Dist[Point3]]
      galaxy ← Galaxy.dist(universe, loc)
    } yield galaxy

  checkAll("Eq[Galaxy]", OrderLaws[Galaxy].eqv)
}
