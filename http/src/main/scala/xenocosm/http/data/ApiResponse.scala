package xenocosm.http.data

import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist

final case class ApiResponse(version:Int)

object ApiResponse {

  trait Instances extends JsonHal[ApiResponse] {
    import io.circe.syntax._

    def cleanBase(base:ApiResponse):Json = Json.Null

    def baseFromSelfLink(hcursor:HCursor):Decoder.Result[ApiResponse] =
      selfPath(hcursor).flatMap({
        case Root / "v1" => Right(ApiResponse(1))
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    implicit val apiResponseHasDist:Dist[ApiResponse] =
      Dist.constant(ApiResponse(1))

    implicit val apiResponseHasJsonEncoder:Encoder[ApiResponse] =
      Encoder.instance(_ => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> "/v1".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:multiverse" -> Json.obj(
            "href" -> "/v1/multiverse".asJson
          )
        )
      ))

    implicit val apiResponseHasJsonDecoder:Decoder[ApiResponse] =
      Decoder.instance { hcur =>
        for {
          api <- baseFromSelfLink(hcur)
        } yield api
      }
  }
  object instances extends Instances
}
