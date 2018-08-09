package xenocosm.json

import io.circe._
import squants.motion.{Velocity, VolumeFlow}
import squants.space.{Length, Volume}

import xenocosm.data._

trait ShipModuleJson {
  import io.circe.syntax._
  import interop.squants.json.instances._
  import cargo._

  implicit val shipModuleHasJsonEncoder:Encoder[ShipModule] =
    Encoder.instance({
      case EmptyModule => Json.obj("module" -> "empty".asJson)
      case CargoHold(cargo) =>
        Json.obj(
          "module" -> "cargo".asJson,
          "contents" -> cargo.asJson
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
      case engine @ Engine(speed, consumption) =>
        Json.obj(
          "module" -> "engine".asJson,
          "speed" -> speed.asJson,
          "consumption" -> consumption.asJson,
          "fuelEfficiency" -> ShipModule.fuelEfficiency(engine).asJson
        )
    })

  implicit val shipModuleHasJsonDecoder:Decoder[ShipModule] =
    Decoder.instance { hcur =>
      hcur.downField("module").as[String].flatMap({
        case "empty" => Right(EmptyModule)
        case "cargo" =>
          for {
            contents <- hcur.downField("contents").as[Map[Cargo, Volume]]
          } yield CargoHold(contents)
        case "fuel" =>
          for {
            used <- hcur.downField("used").as[Volume]
            unused <- hcur.downField("unused").as[Volume]
          } yield FuelTank(used, unused)
        case "navigation" =>
          for {
            range <- hcur.downField("range").as[Length]
          } yield Navigation(range)
        case "engine" =>
          for {
            speed <- hcur.downField("speed").as[Velocity]
            consumption <- hcur.downField("consumption").as[VolumeFlow]
          } yield Engine(speed, consumption)
        case _ => Left(DecodingFailure.apply("unrecognized module", List.empty[CursorOp]))
      })
    }
}
