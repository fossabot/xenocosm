package xenocosm.json

import java.util.UUID
import io.circe._

import xenocosm.data.{ElapsedTime, Ship, Trader}

trait TraderJson {
  import io.circe.syntax._
  import elapsedTime._
  import ship._

  implicit val traderHasJsonEncoder:Encoder[Trader] =
    Encoder.instance(trader =>
      Json.obj(
        "uuid" -> trader.uuid.asJson,
        "ship" -> trader.ship.asJson,
        "elapsed" -> trader.elapsed.asJson
      )
    )

  implicit val traderHasJsonDecoder:Decoder[Trader] =
    Decoder.instance { hcur =>
      for {
        uuid <- hcur.downField("uuid").as[UUID]
        ship <- hcur.downField("ship").as[Ship]
        elapsed <- hcur.downField("elapsed").as[ElapsedTime]
      } yield Trader(uuid, ship, elapsed)
    }
}
