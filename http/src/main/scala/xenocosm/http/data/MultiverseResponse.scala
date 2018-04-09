package xenocosm.http
package data

import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist

case object MultiverseResponse {

  trait Instances extends JsonHal[MultiverseResponse.type] {
    import io.circe.syntax._

    def cleanBase(base:MultiverseResponse.type):Json = Json.Null

    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[MultiverseResponse.type] =
      selfPath(hcursor).flatMap({
        case Root / "v1" / "multiverse" => Right(MultiverseResponse)
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    implicit val multiverseResponseHasDist:Dist[MultiverseResponse.type] =
      Dist.constant(MultiverseResponse)

    implicit val multiverseResponseHasJsonEncoder:Encoder[MultiverseResponse.type] =
      Encoder.instance(_ => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> "/v1/multiverse".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:universe" -> Json.obj(
            "href" -> "/v1/multiverse/{uuid}".asJson,
            "templated" -> Json.True
          )
        )
      ))

    implicit val multiverseResponseHasJsonDecoder:Decoder[MultiverseResponse.type] =
      Decoder.instance { hcur =>
        for {
          multiverse <- baseFromSelfLink(hcur)
        } yield multiverse
      }
  }
  object instances extends Instances
}
