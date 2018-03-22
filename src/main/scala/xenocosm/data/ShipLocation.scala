package xenocosm
package data

import galaxique.data._
import io.circe.{Decoder, Encoder, Json}

sealed trait ShipLocation
case object Lost extends ShipLocation
final case class IntergalacticSpace(universe:Universe, coords:Point3) extends ShipLocation
final case class InterstellarSpace(galaxy:Galaxy, coords:Point3) extends ShipLocation
final case class InterplanetarySpace(star:Star, coords:Point3) extends ShipLocation
final case class Docked(planet:Planet) extends ShipLocation

object ShipLocation {
  import io.circe.syntax._

  trait Instances {
    implicit val shipLocationHasJsonEncoder:Encoder[ShipLocation] =
      Encoder.instance({
        case Lost => Json.Null
        case IntergalacticSpace(universe, coords) =>
          Json.obj(
            "universe" -> universe.asJson,
            "coords" -> coords.asJson
          )
        case InterstellarSpace(galaxy, coords) =>
          Json.obj(
            "galaxy" -> galaxy.asJson,
            "coords" -> coords.asJson
          )
        case InterplanetarySpace(star, coords) =>
          Json.obj(
            "star" -> star.asJson,
            "coords" -> coords.asJson
          )
        case Docked(planet) => Json.obj("planet" -> planet.asJson)
      })

    private val lostHasJsonDecoder:Decoder[ShipLocation] =
      Decoder.decodeNone.map(_ => Lost)

    private val intergalacticSpaceHasJsonDecoder:Decoder[ShipLocation] =
      Decoder.instance { hcur =>
        for {
          universe <- hcur.downField("universe").as[Universe]
          coords <- hcur.downField("coords").as[Point3]
        } yield IntergalacticSpace(universe, coords)
      }

    private val interstellarSpaceHasJsonDecoder:Decoder[ShipLocation] =
      Decoder.instance { hcur =>
        for {
          galaxy <- hcur.downField("galaxy").as[Galaxy]
          coords <- hcur.downField("coords").as[Point3]
        } yield InterstellarSpace(galaxy, coords)
      }

    private val interplanetarySpaceHasJsonDecoder:Decoder[ShipLocation] =
      Decoder.instance { hcur =>
        for {
          star <- hcur.downField("star").as[Star]
          coords <- hcur.downField("coords").as[Point3]
        } yield InterplanetarySpace(star, coords)
      }

    private val dockedHasJsonDecoder:Decoder[ShipLocation] =
      Decoder.instance { hcur =>
        for {
          planet <- hcur.downField("planet").as[Planet]
        } yield Docked(planet)
      }

    implicit val shipLocationHasJsonDecoder:Decoder[ShipLocation] =
      lostHasJsonDecoder
        .or(intergalacticSpaceHasJsonDecoder)
        .or(interstellarSpaceHasJsonDecoder)
        .or(interplanetarySpaceHasJsonDecoder)
        .or(dockedHasJsonDecoder)
  }
  object instances extends Instances
}
