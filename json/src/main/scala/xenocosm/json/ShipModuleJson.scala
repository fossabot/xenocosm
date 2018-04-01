package xenocosm.json

import io.circe._
import squants.space.{Length, Volume}

import xenocosm.data._

trait ShipModuleJson {
  import io.circe.syntax._
  import galaxique.json.interop.length._
  import galaxique.json.interop.volume._

  implicit val shipModuleHasJsonEncoder:Encoder[ShipModule] =
    Encoder.instance({
      case EmptyModule => Json.obj("module" -> "empty".asJson)
      case CargoHold(used, unused) =>
        Json.obj(
          "module" -> "cargo".asJson,
          "used" -> used.asJson,
          "unused" -> unused.asJson
        )
      case FuelTank(used, unused) =>
        Json.obj(
          "module" -> "fuel".asJson,
          "used" -> used.asJson,
          "unused" -> unused.asJson
        )
      case Navigation(range) =>
        Json.obj(
          "module" -> "navigation".asJson,
          "range" -> range.asJson
        )
    })

  implicit val shipModuleHasJsonDecoder:Decoder[ShipModule] =
    Decoder.instance { hcur =>
      hcur.downField("module").as[String].flatMap({
        case "empty" => Right(EmptyModule)
        case "cargo" =>
          for {
            used <- hcur.downField("used").as[Volume]
            unused <- hcur.downField("unused").as[Volume]
          } yield CargoHold(used, unused)
        case "fuel" =>
          for {
            used <- hcur.downField("used").as[Volume]
            unused <- hcur.downField("unused").as[Volume]
          } yield FuelTank(used, unused)
        case "navigation" =>
          for {
            range <- hcur.downField("range").as[Length]
          } yield Navigation(range)
        case _ => Left(DecodingFailure.apply("unrecognized module", List.empty[CursorOp]))
      })
    }
}
