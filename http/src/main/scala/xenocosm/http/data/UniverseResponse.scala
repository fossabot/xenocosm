package xenocosm.http
package data

import galaxique.data.{Galaxy, Point3, Universe}
import galaxique.implicits._
import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist
import squants.space.{Length, Parsecs}

final case class UniverseResponse(universe:Universe, loc:Point3, range:Length) {
  lazy val galaxies:Iterator[Galaxy] = universe.nearby(loc, range)
}

object UniverseResponse {
  trait Instances extends JsonHal[Universe] {
    import galaxique.json.interop.length._
    import galaxique.json.point3._
    import galaxique.json.universe._
    import io.circe.syntax._

    def cleanBase(universe:Universe):Json =
      universe.asJson.hcursor
        .downField("uuid")
        .delete.top
        .getOrElse(Json.Null)

    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[Universe] =
      selfPath(hcursor).flatMap({
        case Root / ⎈(uuid) => Right(Universe(uuid))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    private lazy val origin = Point3(Parsecs(0), Parsecs(0), Parsecs(0))

    implicit val universeResponseHasDist:Dist[UniverseResponse] =
      Dist[Universe].map(universe => UniverseResponse(universe, origin, Parsecs(10000)))

    implicit val universeResponseHasJsonEncoder:Encoder[UniverseResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"/${⎈(res.universe.uuid)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:galaxy" -> res.galaxies.map({galaxy => Json.obj("href" ->
            s"/${⎈(galaxy.universe.uuid)}/${✺(galaxy.loc)}".asJson
          )}).toSeq.asJson
        ),
        "universe" -> cleanBase(res.universe),
        "loc" -> res.loc.asJson,
        "scan-range" -> res.range.asJson
      ))

    implicit val universeResponseHasJsonDecoder:Decoder[UniverseResponse] =
      Decoder.instance { hcur =>
        for {
          universe <- baseFromSelfLink(hcur)
          loc <- hcur.downField("loc").as[Point3]
          range <- hcur.downField("scan-range").as[Length]
        } yield UniverseResponse(universe, loc, range)
      }
  }
  object instances extends Instances
}
