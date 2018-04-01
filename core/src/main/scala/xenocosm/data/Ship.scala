package xenocosm
package data

import java.util.UUID
import spire.random.Dist
import squants.space.{CubicMeters, Parsecs}

final case class Ship(uuid:UUID, loc:ShipLocation, modules:List[ShipModule])

object Ship {
  import galaxique.interop.uuid._
  import ShipLocation.instances._

  lazy val startingLoad:List[ShipModule] =
    List(
      Navigation(Parsecs(1)),
      ShipModule.emptyCargo(CubicMeters(100)),
      ShipModule.emptyFuel(CubicMeters(100)),
      EmptyModule,
      EmptyModule
    )

  trait Instances {
    implicit val shipHasDist:Dist[Ship] =
      for {
        uuid <- Dist[UUID]
        loc <- Dist[ShipLocation]
      } yield Ship(uuid, loc, startingLoad)
  }
  object instances extends Instances
}
