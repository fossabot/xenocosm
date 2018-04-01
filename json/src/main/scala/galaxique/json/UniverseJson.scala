package galaxique.json

import java.util.UUID
import io.circe._
import galaxique.data.Universe

trait UniverseJson {
  import io.circe.syntax._
  import interop.length._

  implicit val universeHasJsonEncode:Encoder[Universe] =
    Encoder.instance(a => Json.obj(
      "uuid" -> a.uuid.asJson,
      "age" -> a.age.asJson,
      "diameter" -> a.diameter.asJson,
      "radius" -> a.radius.asJson
    ))

  implicit val universeHasJsonDecode:Decoder[Universe] =
    Decoder.instance { hcur =>
      for {
        uuid <- hcur.downField("uuid").as[UUID]
      } yield Universe(uuid)
    }
}
