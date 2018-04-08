package xenocosm.http
package data

import galaxique.data.{Galaxy, Planet, Star, Universe}
import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist

final case class PlanetResponse(planet:Planet)

object PlanetResponse {
  trait Instances extends JsonHal[Planet] {
    import Planet.instances._
    import galaxique.json.planet._
    import io.circe.syntax._

    def cleanBase(planet:Planet):Json =
      planet.asJson.hcursor
        .downField("star")
        .deleteGoField("loc")
        .delete.top
        .getOrElse(Json.Null)

    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[Planet] =
      selfPath(hcursor).flatMap({
        case Root / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) => Right(Planet(Star(Galaxy(Universe(uuid), locU), locG), locS))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    implicit val planetResponseHasDist:Dist[PlanetResponse] =
      Dist[Planet].map(PlanetResponse.apply)

    implicit val planetResponseHasJsonEncoder:Encoder[PlanetResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"/${⎈(res.planet.star.galaxy.universe.uuid)}/${✺(res.planet.star.galaxy.loc)}/${✨(res.planet.star.loc)}/${★(res.planet.loc)}".asJson),
          "curies" -> Json.arr(apiCurie)
        ),
        "planet" -> cleanBase(res.planet)
      ))

    implicit val planetResponseHasJsonDecoder:Decoder[PlanetResponse] =
      Decoder.instance { hcur =>
        for {
          planet <- baseFromSelfLink(hcur)
        } yield PlanetResponse(planet)
      }
  }
  object instances extends Instances
}
