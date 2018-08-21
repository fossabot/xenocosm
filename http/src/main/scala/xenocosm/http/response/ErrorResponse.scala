package xenocosm.http
package response

import io.circe._

import xenocosm.XenocosmError

final case class ErrorResponse(error:XenocosmError)

object ErrorResponse {
  import io.circe.syntax._
  import xenocosm.json.xenocosmError._

  trait Instances {

    implicit val errorResponseHasJsonHalEncoder:Encoder[ErrorResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "curies" -> Json.arr(apiCurie)
        ),
        "error" -> res.error.asJson
      ))

    implicit val errorResponseHasJsonHalDecoder:Decoder[ErrorResponse] =
      Decoder.instance(_.downField("error").as[XenocosmError].map(ErrorResponse.apply))
  }

  object instances extends Instances
}
