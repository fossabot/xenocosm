package xenocosm.json

import java.util.UUID
import io.circe._
import galaxique.data._

import xenocosm.data._

trait CosmicLocationJson {
  import io.circe.syntax._
  import galaxique.json.implicits._

  implicit val cosmicLocationHasJsonEncoder:Encoder[CosmicLocation] =
    Encoder.instance({
      case CosmicLocation(uuid, Some(locU), Some(locG), Some(locS)) =>
        Json.obj(
          "uuid" -> uuid.asJson,
          "locU" -> locU.asJson,
          "locG" -> locG.asJson,
          "locS" -> locS.asJson
        )
      case CosmicLocation(uuid, Some(locU), Some(locG), _) =>
        Json.obj(
          "uuid" -> uuid.asJson,
          "locU" -> locU.asJson,
          "locG" -> locG.asJson
        )
      case CosmicLocation(uuid, Some(locU), _, _) =>
        Json.obj(
          "uuid" -> uuid.asJson,
          "locU" -> locU.asJson
        )
      case CosmicLocation(uuid, _, _, _) =>
        Json.obj("uuid" -> uuid.asJson)
    })

  implicit val cosmicLocationHasJsonDecoder:Decoder[CosmicLocation] =
    Decoder.instance { hcur =>
      for {
        uuid <- hcur.downField("uuid").as[UUID]
        locU <- hcur.downField("locU").as[Option[Point3]]
        locG <- hcur.downField("locG").as[Option[Point3]]
        locS <- hcur.downField("locS").as[Option[Point3]]
      } yield CosmicLocation(uuid, locU, locG, locS)
    }
}
