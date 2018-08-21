package xenocosm.http
package response

import io.circe._
import spire.random.Dist

import xenocosm.data.Trader

final case class TraderResponse(trader:Trader)

object TraderResponse {
  import Trader.instances._
  import xenocosm.json.trader._

  trait Instances {
    import io.circe.syntax._

    implicit val createTraderResponseHasDist:Dist[TraderResponse] =
      Dist[Trader].map(TraderResponse.apply)

    implicit val createTraderResponseHasJsonEncoder:Encoder[TraderResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"$apiTrader/${⎈(res.trader.uuid)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:multiverse" -> Json.obj(
            "href" -> apiMultiverse.asJson
          )
        ),
        "trader" -> res.trader.asJson
      ))

    implicit val createTraderResponseHasJsonDecoder:Decoder[TraderResponse] =
      Decoder.instance { _.downField("trader").as[Trader].map(TraderResponse.apply) }
  }
  object instances extends Instances
}


