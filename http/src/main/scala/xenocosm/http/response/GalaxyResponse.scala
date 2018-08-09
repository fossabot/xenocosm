package xenocosm.http
package response

import galaxique.data.{Galaxy, Point3, Star, Universe}
import galaxique.implicits._
import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist
import squants.space.{Length, Parsecs}

final case class GalaxyResponse(galaxy:Galaxy, loc:Point3, range:Length) {
  lazy val stars:Iterator[Star] = galaxy.nearby(loc, range)
}

object GalaxyResponse {
  trait Instances {

    import galaxique.json.galaxy._
    import galaxique.json.point3._
    import interop.squants.json.instances._
    import io.circe.syntax._

    def cleanBase(galaxy:Galaxy):Json =
      galaxy.asJson.hcursor
        .downField("universe")
        .deleteGoField("loc")
        .delete.top
        .getOrElse(Json.Null)

    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[Galaxy] =
      selfPath(hcursor).flatMap({
        case Root / "v1" / "multiverse" / ⎈(uuid) / ✺(locU) => Right(Galaxy(Universe(uuid), locU))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    private lazy val origin = Point3(Parsecs(0), Parsecs(0), Parsecs(0))

    implicit val galaxyResponseHasDist:Dist[GalaxyResponse] =
      Dist[Galaxy].map(galaxy => GalaxyResponse(galaxy, origin, Galaxy.scale))

    implicit val galaxyResponseHasJsonEncoder:Encoder[GalaxyResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"/v1/multiverse/${⎈(res.galaxy.universe.uuid)}/${✺(res.galaxy.loc)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:star" -> res.stars.map(star => Json.obj("href" ->
            s"v1/multiverse//${⎈(star.galaxy.universe.uuid)}/${✺(star.galaxy.loc)}/${✨(star.loc)}".asJson
          )).toSeq.asJson
        ),
        "galaxy" -> cleanBase(res.galaxy),
        "loc" -> res.loc.asJson,
        "scan-range" -> res.range.asJson
      ))

    implicit val galaxyResponseHasJsonDecoder:Decoder[GalaxyResponse] =
      Decoder.instance { hcur =>
        for {
          galaxy <- baseFromSelfLink(hcur)
          loc <- hcur.downField("loc").as[Point3]
          range <- hcur.downField("scan-range").as[Length]
        } yield GalaxyResponse(galaxy, loc, range)
      }
  }
  object instances extends Instances
}
