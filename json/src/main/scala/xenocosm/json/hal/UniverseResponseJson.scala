package xenocosm.json.hal

import java.util.UUID
import galaxique.data.{Galaxy, Point3, Universe}
import io.circe._
import squants.space.Parsecs

import xenocosm.http.data.UniverseResponse

trait UniverseResponseJson extends JsonHal {
  import io.circe.syntax._
  import galaxique.json.interop.length._
  import galaxique.json.point3._
  import galaxique.json.universe._

  implicit val universeResponseHasJsonEncoder:Encoder[UniverseResponse] =
    Encoder.instance(res => Json.obj(
      "_links" -> Json.obj(
        "self" -> Json.obj("href" -> s"/${res.universe.uuid.toString}".asJson),
        "curies" -> Json.arr(apiCurie),
        "api:galaxy" -> locUrls[Galaxy](s"/${res.universe.uuid.toString}", _.loc)(res.galaxies).map(url => Json.obj("href" -> url.asJson)).asJson
      ),
      "universe" -> res.universe.asJson,
      "loc" -> res.loc.asJson,
      "scan-range" -> res.range.asJson
    ))

  implicit val universeResponseHasJsonDecoder:Decoder[UniverseResponse] =
    Decoder.instance { hcur =>
      hcur.downField("_links").downField("self").downField("href").as[String].flatMap({
        case "/" => Right(UniverseResponse(Universe(UUID.randomUUID), Point3.zero, Parsecs(50000)))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })
    }
}
