package xenocosm.json

import java.util.UUID
import io.circe._
import spire.math.UInt

import xenocosm.data.{ForeignID, Identity, Trader}

trait IdentityJson {
  import io.circe.syntax._
  import interop.spire.json.instances._
  import foreignID._
  import trader._

  implicit val identityHasJsonEncoder:Encoder[Identity] =
    Encoder.instance(identity =>
      Json.obj(
        "uuid" -> identity.uuid.asJson,
        "ref" -> identity.ref.map(_.asJson).getOrElse(Json.Null),
        "moves" -> identity.moves.asJson,
        "trader" -> identity.trader.asJson
      )
    )

  implicit val identityHasJsonDecoder:Decoder[Identity] =
    Decoder.instance { hcur =>
        for {
          uuid <- hcur.downField("uuid").as[UUID]
          ref <- hcur.downField("ref").as[Option[ForeignID]]
          moves <- hcur.downField("moves").as[UInt]
          trader <- hcur.downField("trader").as[Option[Trader]]
        } yield Identity(uuid, ref, moves, trader)
    }
}
