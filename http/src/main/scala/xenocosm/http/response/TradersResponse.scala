package xenocosm.http
package response

import io.circe._

import xenocosm.data.Trader

final case class TradersResponse(traders:List[Trader])

object TradersResponse {
  import io.circe.syntax._
  import xenocosm.json.trader._
  import xenocosm.http.syntax.trader._

  trait Instances {

    implicit val tradersResponseHasJsonEncoder:Encoder[TradersResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"/trader".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:trader" -> res.traders.map(x => Map("href" -> x.uri.toString())).asJson
        ),
        "traders" -> res.traders.asJson
      ))

    implicit val tradersResponseHasJsonDecoder:Decoder[TradersResponse] =
      Decoder.instance { _.downField("traders").as[List[Trader]].map(TradersResponse.apply) }
  }
  object instances extends Instances
}
