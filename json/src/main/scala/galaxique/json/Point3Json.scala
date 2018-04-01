package galaxique.json

import io.circe._
import squants.space.Length
import galaxique.data.Point3

trait Point3Json {
  import io.circe.syntax._
  import interop.length._

  implicit val point3HasJsonEncode:Encoder[Point3] =
    Encoder.instance(loc => Json.arr(loc.x.asJson, loc.y.asJson, loc.z.asJson))

  implicit val point3HasJsonDecode:Decoder[Point3] =
    Decoder.instance { hcur =>
      for {
        x <- hcur.downN(0).as[Length]
        y <- hcur.downN(1).as[Length]
        z <- hcur.downN(2).as[Length]
      } yield Point3(x, y, z)
    }
}
