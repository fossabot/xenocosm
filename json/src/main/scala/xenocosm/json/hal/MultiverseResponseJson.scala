package xenocosm.json.hal

import io.circe._
import xenocosm.http.data.MultiverseResponse

trait MultiverseResponseJson extends JsonHal {
  import io.circe.syntax._

  implicit val multiverseResponseHasJsonEncoder:Encoder[MultiverseResponse.type] =
    Encoder.instance(_ => Json.obj(
      "_links" -> Json.obj(
        "self" -> Json.obj("href" -> "/".asJson),
        "curies" -> Json.arr(apiCurie),
        "api:universe" -> Json.obj(
          "href" -> "/{uuid}".asJson,
          "templated" -> Json.True
        )
      )
    ))

  implicit val multiverseResponseHasJsonDecoder:Decoder[MultiverseResponse.type] =
    Decoder.instance { hcur =>
      hcur.downField("_links").downField("self").downField("href").as[String].flatMap({
        case "/" => Right(MultiverseResponse)
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })
    }
}
