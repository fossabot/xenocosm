package xenocosm.json

import io.circe._

import xenocosm.data.ForeignID

trait ForeignIDJson {
  implicit val foreignIDHasJsonEncoder:Encoder[ForeignID] =
    Encoder.encodeString.contramap(_.underlying)

  implicit val foreignIDHasJsonDecoder:Decoder[ForeignID] =
    Decoder.decodeString.map(ForeignID.apply)
}
