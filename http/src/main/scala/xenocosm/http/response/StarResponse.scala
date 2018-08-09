package xenocosm.http
package response

import galaxique.data._
import galaxique.implicits._
import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist
import squants.space.{AstronomicalUnits, Length}

final case class StarResponse(star:Star, loc:Point3, range:Length) {
  lazy val planets:Iterator[Planet] = star.nearby(loc, range)
}

object StarResponse {
  trait Instances {
    import galaxique.json.point3._
    import galaxique.json.star._
    import interop.squants.json.instances._
    import io.circe.syntax._

    def cleanBase(star:Star):Json =
      star.asJson.hcursor
        .downField("galaxy")
        .deleteGoField("loc")
        .delete.top
        .getOrElse(Json.Null)

    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[Star] =
      selfPath(hcursor).flatMap({
        case Root / "v1" / "multiverse" / ⎈(uuid) / ✺(locU) / ✨(locG) => Right(Star(Galaxy(Universe(uuid), locU), locG))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    private lazy val origin = Point3(AstronomicalUnits(0), AstronomicalUnits(0), AstronomicalUnits(0))

    implicit val starResponseHasDist:Dist[StarResponse] =
      Dist[Star].map(star => StarResponse(star, origin, Star.scale))

    implicit val starResponseHasJsonEncoder:Encoder[StarResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"/v1/multiverse/${⎈(res.star.galaxy.universe.uuid)}/${✺(res.star.galaxy.loc)}/${✨(res.star.loc)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:planet" -> res.planets.map(planet => Json.obj("href" ->
            s"/v1/multiverse/${⎈(planet.star.galaxy.universe.uuid)}/${✺(planet.star.galaxy.loc)}/${✨(planet.star.loc)}/${★(planet.loc)}".asJson
          )).toSeq.asJson
        ),
        "star" -> cleanBase(res.star),
        "loc" -> res.loc.asJson,
        "scan-range" -> res.range.asJson
      ))

    implicit val starResponseHasJsonDecoder:Decoder[StarResponse] =
      Decoder.instance { hcur =>
        for {
          star <- baseFromSelfLink(hcur)
          loc <- hcur.downField("loc").as[Point3]
          range <- hcur.downField("scan-range").as[Length]
        } yield StarResponse(star, loc, range)
      }
  }
  object instances extends Instances
}
