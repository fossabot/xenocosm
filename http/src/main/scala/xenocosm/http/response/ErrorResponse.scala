package xenocosm.http
package response

import io.circe._

import xenocosm.XenocosmError
import xenocosm.data.Trader

final case class ErrorResponse(trader:Trader, error:XenocosmError)

object ErrorResponse {
  import io.circe.syntax._
  import xenocosm.json.trader._
  import xenocosm.json.xenocosmError._
  import xenocosm.http.syntax.cosmicLocation._
  import xenocosm.http.syntax.trader._

  trait Instances {

    implicit val errorResponseHasJsonEncoder:Encoder[ErrorResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> res.trader.uri.toString().asJson),
          "curies" -> Json.arr(apiCurie),
          "api:multiverse" -> Json.obj(
            "href" -> res.trader.ship.loc.uri.toString().asJson
          )
        ),
        "trader" -> res.trader.asJson,
        "error" -> res.error.asJson
      ))
  }
  object instances extends Instances
}
