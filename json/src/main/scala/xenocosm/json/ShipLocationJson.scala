package xenocosm.json

import io.circe._
import galaxique.data._

import xenocosm.data._

trait ShipLocationJson {
  import io.circe.syntax._
  import galaxique.json.implicits._

  implicit val shipLocationHasJsonEncoder:Encoder[ShipLocation] =
    Encoder.instance({
      case Lost => Json.obj("space" -> "lost".asJson)
      case IntergalacticSpace(universe, coords) =>
        Json.obj(
          "space" -> "intergalactic".asJson,
          "universe" -> universe.asJson,
          "coords" -> coords.asJson
        )
      case InterstellarSpace(galaxy, coords) =>
        Json.obj(
          "space" -> "interstellar".asJson,
          "galaxy" -> galaxy.asJson,
          "coords" -> coords.asJson
        )
      case InterplanetarySpace(star, coords) =>
        Json.obj(
          "space" -> "interplanetary".asJson,
          "star" -> star.asJson,
          "coords" -> coords.asJson
        )
      case Docked(planet) =>
        Json.obj(
          "space" -> "docked".asJson,
          "planet" -> planet.asJson
        )
    })

  implicit val shipLocationHasJsonDecoder:Decoder[ShipLocation] =
    Decoder.instance { hcur =>
      hcur.downField("space").as[String].flatMap({
        case "lost" => Right(Lost)
        case "intergalactic" =>
          for {
            universe <- hcur.downField("universe").as[Universe]
            coords <- hcur.downField("coords").as[Point3]
          } yield IntergalacticSpace(universe, coords)
        case "interstellar" =>
          for {
            galaxy <- hcur.downField("galaxy").as[Galaxy]
            coords <- hcur.downField("coords").as[Point3]
          } yield InterstellarSpace(galaxy, coords)
        case "interplanetary" =>
          for {
            star <- hcur.downField("star").as[Star]
            coords <- hcur.downField("coords").as[Point3]
          } yield InterplanetarySpace(star, coords)
        case "docked" =>
          for {
            planet <- hcur.downField("planet").as[Planet]
          } yield Docked(planet)
        case _ => Left(DecodingFailure.apply("unrecognized location", List.empty[CursorOp]))
      })
    }
}
