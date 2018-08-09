package interop.java
package util

import java.util.UUID
import spire.random.Dist

trait UuidInstances {
  implicit val uuidHasDist:Dist[UUID] =
    for {
      mostSigBits <- Dist.long
      leastSigBits <- Dist.long
    } yield new UUID(mostSigBits, leastSigBits)
}
