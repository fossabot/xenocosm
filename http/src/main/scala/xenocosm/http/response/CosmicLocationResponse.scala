package xenocosm.http
package response

import io.circe.{Decoder, Encoder, Json}

import xenocosm.data.CosmicLocation

final case class CosmicLocationResponse(loc:CosmicLocation)

object CosmicLocationResponse {
  import org.http4s.dsl.io._
  import io.circe.syntax._

  def toPath(loc:CosmicLocation):String =
    (loc.galaxy.map(_.loc), loc.galaxy.map(_.loc), loc.galaxy.map(_.loc)) match {
      case (Some(locU), Some(locG), Some(locS)) => s"/${✺(locU)}/${✨(locG)}/${★(locS)}"
      case (Some(locU), Some(locG), None) => s"/${✺(locU)}/${✨(locG)}}"
      case (Some(locU), None, _) => s"/${✺(locU)}}"
      case _ => ""
    }


  trait Instances {
    implicit val cosmicLocationResponseHasJsonEncoder:Encoder[CosmicLocationResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"$apiMultiverse/${toPath(res.loc)}".asJson),
          "curies" -> Json.arr(apiCurie)
        )
      ))

    implicit val cosmicLocationResponseHasJsonDecoder:Decoder[CosmicLocationResponse] =
      Decoder.instance { hcur =>
        hcur
          .downField("_links")
          .downField("self")
          .downField("href")
          .as[String]
          .map(Path.apply)
          .map({
            case Root / ⎈(uuid) / ✺(locU) / ✨(locG) / ★(locS) =>
              CosmicLocation(uuid, Some(locU), Some(locG), Some(locS))
            case Root / ⎈(uuid) / ✺(locU) / ✨(locG) =>
              CosmicLocation(uuid, Some(locU), Some(locG), None)
            case Root / ⎈(uuid) / ✺(locU) =>
              CosmicLocation(uuid, Some(locU), None, None)
            case Root / ⎈(uuid) =>
              CosmicLocation(uuid, None, None, None)
          })
          .map(CosmicLocationResponse.apply)
      }

  }
  object instances extends Instances
}
