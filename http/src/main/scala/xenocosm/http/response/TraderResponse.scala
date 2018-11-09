package xenocosm.http
package response

import io.circe._

import xenocosm.data.Trader

final case class TraderResponse(trader:Trader)

object TraderResponse {
  import io.circe.syntax._
  import xenocosm.json.trader._
  import xenocosm.http.syntax.cosmicLocation._
  import xenocosm.http.syntax.trader._

  trait Instances {

    implicit val traderResponseHasJsonEncoder:Encoder[TraderResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> res.trader.uri.toString().asJson),
          "curies" -> Json.arr(apiCurie),
          "api:multiverse" -> Json.obj(
            "href" -> res.trader.ship.loc.uri.toString().asJson
          )
        ),
        "trader" -> res.trader.asJson
      ))

    implicit val traderResponseHasJsonDecoder:Decoder[TraderResponse] =
      Decoder.instance { _.downField("trader").as[Trader].map(TraderResponse.apply) }
  }
  object instances extends Instances
}
