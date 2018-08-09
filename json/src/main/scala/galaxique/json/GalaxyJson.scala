package galaxique.json

import io.circe._
import galaxique.data.{Galaxy, Point3, Universe}

trait GalaxyJson {
  import io.circe.syntax._
  import interop.squants.json.instances._
  import point3._
  import universe._

  implicit val galaxyHasJsonEncode:Encoder[Galaxy] =
    Encoder.instance(a => Json.obj(
      "universe" -> a.universe.asJson,
      "loc" -> a.loc.asJson,
      "luminosity" -> a.luminosity.asJson,
      "diameter" -> a.diameter.asJson,
      "mass" -> a.mass.asJson,
      "radius" -> a.radius.asJson
    ))

  implicit val galaxyHasJsonDecode:Decoder[Galaxy] =
    Decoder.instance { hcur =>
      for {
        universe <- hcur.downField("universe").as[Universe]
        loc <- hcur.downField("loc").as[Point3]
      } yield Galaxy(universe, loc)
    }
}
