package xenocosm.json

import java.util.UUID
import io.circe._

import xenocosm.data.{CosmicLocation, Ship, ShipModule}

trait ShipJson {
  import io.circe.syntax._
  import cosmicLocation._
  import shipModule._

  implicit val shipHasJsonEncoder:Encoder[Ship] =
    Encoder.instance(ship =>
      Json.obj(
        "uuid" -> ship.uuid.asJson,
        "loc" -> ship.loc.asJson,
        "modules" -> ship.modules.asJson
      )
    )

  implicit val shipHasJsonDecoder:Decoder[Ship] =
    Decoder.instance { hcur =>
        for {
          uuid <- hcur.downField("uuid").as[UUID]
          loc <- hcur.downField("loc").as[CosmicLocation]
          modules <- hcur.downField("modules").as[List[ShipModule]]
        } yield Ship(uuid, loc, modules)
    }
}
