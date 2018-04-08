package xenocosm.http
package data

import io.circe._
import org.http4s.dsl.impl.Root
import spire.random.Dist

case object MultiverseResponse {

  trait Instances extends JsonHal[Unit] {
    import io.circe.syntax._

    def cleanBase(base:Unit):Json = Json.Null
    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[Unit] =
      Decoder.decodeUnit(hcursor)

    implicit val multiverseResponseHasDist:Dist[MultiverseResponse.type] =
      Dist.constant(MultiverseResponse)

    implicit val multiverseResponseHasJsonEncoder:Encoder[MultiverseResponse.type] =
      Encoder.instance(_ => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> "/".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:universe" -> Json.obj(
            "href" -> "/{uuid}".asJson,
            "templated" -> Json.True
          )
        )
      ))

    implicit val multiverseResponseHasJsonDecoder:Decoder[MultiverseResponse.type] =
      Decoder.instance { hcur =>
        selfPath(hcur).flatMap({
          case Root => Right(MultiverseResponse)
          case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
        })
      }
  }
  object instances extends Instances
}
