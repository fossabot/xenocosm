package xenocosm.http
package response

import galaxique.data.{Galaxy, Planet, Star, Universe}
import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist

final case class PlanetResponse(planet:Planet)

object PlanetResponse {
  trait Instances {
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
        case Root / "v1" / "multiverse" / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) => Right(Planet(Star(Galaxy(Universe(uuid), locU), locG), locS))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    implicit val planetResponseHasDist:Dist[PlanetResponse] =
      Dist[Planet].map(PlanetResponse.apply)

    implicit val planetResponseHasJsonEncoder:Encoder[PlanetResponse] =
      Encoder.instance({ res =>
        val uuid = res.planet.star.galaxy.universe.uuid
        val locU = res.planet.star.galaxy.loc
        val locG = res.planet.star.loc
        val locS = res.planet.loc
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj("href" -> s"/v1/multiverse/${⎈(uuid)}/${✺(locU)}/${✨(locG)}/${★(locS)}".asJson),
            "curies" -> Json.arr(apiCurie)
          ),
          "planet" -> cleanBase(res.planet)
        )
      })

    implicit val planetResponseHasJsonDecoder:Decoder[PlanetResponse] =
      Decoder.instance { hcur =>
        for {
          planet <- baseFromSelfLink(hcur)
        } yield PlanetResponse(planet)
      }
  }
  object instances extends Instances
}
