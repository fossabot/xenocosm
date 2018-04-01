package xenocosm
package data

import java.util.UUID
import spire.random.Dist

final case class Trader(uuid:UUID, ship:Ship)

object Trader {
  import galaxique.interop.uuid._
  import Ship.instances._

  trait Instances {
    implicit val traderHasDist:Dist[Trader] =
      for {
        uuid <- Dist[UUID]
        ship <- Dist[Ship]
      } yield Trader(uuid, ship)

  }
  object instances extends Instances
}
