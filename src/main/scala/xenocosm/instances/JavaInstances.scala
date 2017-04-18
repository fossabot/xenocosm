package xenocosm
package instances

import java.util.UUID
import spire.random.Dist

object JavaInstances {
  trait Instances {
    implicit val uuidHasDist:Dist[UUID] = Dist.array[Long](2, 2).map(xs ⇒ new UUID(xs(0), xs(1)))
  }
  object instances extends Instances
}
