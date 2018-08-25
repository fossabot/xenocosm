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
        "local" -> elapsed.local.asJson,
        "reference" -> elapsed.reference.asJson
      )
    })

  implicit val elapsedTimeHasJsonDecoder:Decoder[ElapsedTime] =
    Decoder.instance { hcur =>
      for {
        local <- hcur.downField("local").as[Time]
        reference <- hcur.downField("reference").as[Time]
      } yield ElapsedTime(local, reference)
    }
}
