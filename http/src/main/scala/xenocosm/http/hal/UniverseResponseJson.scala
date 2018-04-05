package xenocosm.http
package hal

import io.circe._
import squants.space.Length

import galaxique.data.{Point3, Universe}
import xenocosm.http.data.UniverseResponse

trait UniverseResponseJson extends JsonHal {
  import galaxique.json.interop.length._
  import galaxique.json.point3._
  import galaxique.json.universe._
  import io.circe.syntax._

  implicit val universeResponseHasJsonEncoder:Encoder[UniverseResponse] =
    Encoder.instance(res => Json.obj(
      "_links" -> Json.obj(
        "self" -> Json.obj("href" -> s"/${res.universe.uuid.toString}".asJson),
        "curies" -> Json.arr(apiCurie),
        "api:galaxy" -> res.galaxies.map(galaxy => Json.obj("href" ->
          s"/${♠(galaxy.universe)}/${♣(galaxy.loc)}".asJson
        )).toSeq.asJson
      ),
      "universe" -> res.universe.asJson,
      "loc" -> res.loc.asJson,
      "scan-range" -> res.range.asJson
    ))

  implicit val universeResponseHasJsonDecoder:Decoder[UniverseResponse] =
    Decoder.instance { hcur =>
      for {
        universe <- hcur.downField("universe").as[Universe]
        loc <- hcur.downField("loc").as[Point3]
        range <- hcur.downField("scan-range").as[Length]
      } yield UniverseResponse(universe, loc, range)
    }
}
