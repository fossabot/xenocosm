package xenocosm.json

import io.circe._

import xenocosm.data._

trait CargoJson {
  import io.circe.syntax._

  implicit val cargoHasKeyEncoder:KeyEncoder[Cargo] =
    KeyEncoder.instance({
      case Vacuum => "vacuum"
    })

  implicit val cargoHasKeyDecoder:KeyDecoder[Cargo] =
    KeyDecoder[String].map({ _ => Vacuum })

  implicit val cargoHasJsonEncoder:Encoder[Cargo] =
    Encoder.instance({
      case Vacuum => "vacuum".asJson
    })

  implicit val cargoHasJsonDecoder:Decoder[Cargo] =
    Decoder[String].map({ _ => Vacuum })
}
