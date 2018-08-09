package xenocosm.json
package command

import io.circe._
import spire.math.UInt

import xenocosm.MoveShip
import xenocosm.data.{CosmicLocation, Ship}

trait MoveShipJson {
  import io.circe.syntax._
  import interop.spire.json.instances._
  import xenocosm.json.ship._
  import xenocosm.json.cosmicLocation._

  implicit val moveShipHasJsonEncoder:Encoder[MoveShip] =
    Encoder.instance(cmd =>
      Json.obj(
        "moves" -> cmd.moves.asJson,
        "ship" -> cmd.ship.asJson,
        "loc" -> cmd.loc.asJson
      )
    )

  implicit val moveShipHasJsonDecoder:Decoder[MoveShip] =
    Decoder.instance { hcur =>
      for {
        moves <- hcur.downField("moves").as[UInt]
        ship <- hcur.downField("ship").as[Ship]
        loc <- hcur.downField("loc").as[CosmicLocation]
      } yield MoveShip(moves, ship, loc)
    }

}
