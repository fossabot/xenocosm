package galaxique.json

import io.circe._
import galaxique.data.{Planet, Point3, Star}

trait PlanetJson {
  import io.circe.syntax._
  import interop.squants.json.instances._
  import star._
  import point3._

  implicit val planetHasJsonEncode:Encoder[Planet] =
    Encoder.instance(a => Json.obj(
      "star" -> a.star.asJson,
      "loc" -> a.loc.asJson,
      "radius" -> a.radius.asJson,
      "mass" -> a.mass.asJson,
      "eccentricity" -> a.eccentricity.asJson,
      "semiMajorAxis" -> a.semiMajorAxis.asJson,
      "semiMinorAxis" -> a.semiMinorAxis.asJson,
      "volume" -> a.volume.asJson,
      "density" -> a.density.asJson,
      "orbitalPeriod" -> a.orbitalPeriod.asJson
    ))

  implicit val planetHasJsonDecode:Decoder[Planet] =
    Decoder.instance { hcur =>
      for {
        star <- hcur.downField("star").as[Star]
        loc <- hcur.downField("loc").as[Point3]
      } yield Planet(star, loc)
    }
}
