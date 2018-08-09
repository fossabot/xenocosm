package xenocosm.http
package response

import java.util.UUID
import io.circe._
import org.http4s.dsl.impl._
import spire.random.Dist

import xenocosm.data.{Ship, Trader}

final case class TraderResponse(trader:Trader)

object TraderResponse {
  import Trader.instances._
  import xenocosm.json.ship._
  import xenocosm.json.trader._

  trait Instances {
    import io.circe.syntax._

    def uuidFromSelfLink(hcursor:HCursor):Decoder.Result[UUID] =
      selfPath(hcursor).flatMap({
        case Root / "v1" / "trader" / ⎈(uuid) => Right(uuid)
        case _ => Left(DecodingFailure.apply("unrecognized response type", List.empty[CursorOp]))
      })

    def cleanBase(trader:Trader):Json =
      trader.asJson.hcursor
        .downField("uuid")
        .delete.top
        .getOrElse(Json.Null)

    implicit val createTraderResponseHasDist:Dist[TraderResponse] =
      Dist[Trader].map(TraderResponse.apply)

    implicit val createTraderResponseHasJsonEncoder:Encoder[TraderResponse] =
      Encoder.instance(res => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> s"/v1/trader/${⎈(res.trader.uuid)}".asJson),
          "curies" -> Json.arr(apiCurie),
          "api:multiverse" -> Json.obj(
            "href" -> "/v1/multiverse".asJson
          )
        ),
        "trader" -> cleanBase(res.trader)
      ))

    implicit val createTraderResponseHasJsonDecoder:Decoder[TraderResponse] =
      Decoder.instance { hcur =>
        for {
          uuid <- uuidFromSelfLink(hcur)
          ship <- hcur.downField("trader").downField("ship").as[Ship]
        } yield TraderResponse(Trader(uuid, ship))
      }
  }
  object instances extends Instances
}


