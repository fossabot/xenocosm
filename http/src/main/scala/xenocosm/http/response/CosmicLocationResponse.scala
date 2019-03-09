package xenocosm.http
package response

import io.circe.{Decoder, Encoder, Json}
import galaxique.json.implicits._

import xenocosm.data.CosmicLocation
import xenocosm.json.cosmicLocation._

final case class CosmicLocationResponse(loc:CosmicLocation) {
  def path: String = CosmicLocationResponse.toPath(loc)
}

object CosmicLocationResponse {
  import org.http4s.dsl.io._
  import io.circe.syntax._

  def toPath(loc:CosmicLocation):String =
    (loc.locU, loc.locG, loc.locS) match {
      case (Some(locU), Some(locG), Some(locS)) =>
        s"/multiverse/${⎈(loc.universe.uuid)}/${✺(locU)}/${✨(locG)}/${★(locS)}"
      case (Some(locU), Some(locG), _) =>
        s"/multiverse/${⎈(loc.universe.uuid)}/${✺(locU)}/${✨(locG)}}"
      case (Some(locU), _, _) =>
        s"/multiverse/${⎈(loc.universe.uuid)}/${✺(locU)}}"
      case _ =>
        s"/multiverse/${⎈(loc.universe.uuid)}"
    }

  trait Instances {
    implicit val cosmicLocationResponseHasJsonEncoder:Encoder[CosmicLocationResponse] =
      Encoder.instance(res => {
        val meta = Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj("href" -> res.path.asJson),
            "curies" -> Json.arr(apiCurie)
          )
        )

        val loc = (res.loc.galaxy, res.loc.star, res.loc.planet) match {
          case (_, _, Some(planet)) =>
            Json.obj("planet" -> planet.asJson)
          case (_, Some(star), _) =>
            Json.obj("star" -> star.asJson)
          case (Some(galaxy), _, _) =>
            Json.obj("galaxy" -> galaxy.asJson)
          case _ if res.loc.inIntergalacticSpace =>
            Json.obj("intergalactic" -> res.loc.asJson)
          case _ if res.loc.inInterstellarSpace =>
            Json.obj("interstellar" -> res.loc.asJson)
          case _ if res.loc.inInterplanetarySpace =>
            Json.obj("interplanetary" -> res.loc.asJson)
        }

        meta deepMerge loc deepMerge Json.obj("placeName" -> res.loc.placeName.asJson)
      })

    implicit val cosmicLocationResponseHasJsonDecoder:Decoder[CosmicLocationResponse] =
      Decoder.instance { hcur =>
        hcur
          .downField("_links")
          .downField("self")
          .downField("href")
          .as[String]
          .map(Path.apply)
          .map({
            case Root / "multiverse" / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) =>
              CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))
            case Root / "multiverse" / ⎈(uuid) / ✺(locU) / ✨(locG) =>
              CosmicLocation(uuid, Some(locU), Some(locG), None)
            case Root / "multiverse" / ⎈(uuid) / ✺(locU) =>
              CosmicLocation(uuid, Some(locU), None, None)
            case Root / "multiverse" / ⎈(uuid) =>
              CosmicLocation(uuid, None, None, None)
          })
          .map(CosmicLocationResponse.apply)
      }
  }
  object instances extends Instances
}
