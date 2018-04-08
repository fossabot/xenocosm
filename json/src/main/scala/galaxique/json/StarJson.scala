package galaxique.json

import io.circe._
import galaxique.data.{Galaxy, Point3, Star}

trait StarJson {
  import io.circe.syntax._
  import interop.density._
  import interop.length._
  import interop.mass._
  import interop.power._
  import interop.temperature._
  import interop.volume._
  import galaxy._
  import point3._
  import morganKeenan._

  implicit val starHasJsonEncode:Encoder[Star] =
    Encoder.instance(a => Json.obj(
      "galaxy" -> a.galaxy.asJson,
      "loc" -> a.loc.asJson,
      "morgan-keenan" -> a.mk.asJson,
      "mass" -> a.mass.asJson,
      "luminosity" -> a.luminosity.asJson,
      "radius" -> a.radius.asJson,
      "temperature" -> a.temperature.asJson,
      "μ" -> a.μ.asJson,
      "volume" -> a.volume.asJson,
      "density" -> a.density.asJson
    ))

  implicit val starHasJsonDecode:Decoder[Star] =
    Decoder.instance { hcur =>
      for {
        galaxy <- hcur.downField("galaxy").as[Galaxy]
        loc <- hcur.downField("loc").as[Point3]
      } yield Star(galaxy, loc)
    }
}
