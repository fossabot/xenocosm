package xenocosm.json

import io.circe._
import squants.time.Time

import xenocosm.data._

trait ElapsedTimeJson {
  import io.circe.syntax._
  import interop.squants.json.instances._

  implicit val elapsedTimeHasJsonEncoder:Encoder[ElapsedTime] =
    Encoder.instance({ elapsed =>
      Json.obj(
        "moving" -> elapsed.moving.asJson,
        "rest" -> elapsed.rest.asJson
      )
    })

  implicit val elapsedTimeHasJsonDecoder:Decoder[ElapsedTime] =
    Decoder.instance { hcur =>
      for {
        moving <- hcur.downField("moving").as[Time]
        rest <- hcur.downField("rest").as[Time]
      } yield ElapsedTime(moving, rest)
    }
}
