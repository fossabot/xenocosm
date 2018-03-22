package xenocosm
package data

import java.util.UUID

final case class Ship(uuid:UUID, loc:ShipLocation, modules:List[ShipModule])
