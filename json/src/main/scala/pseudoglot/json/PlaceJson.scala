package pseudoglot.json

import cats.implicits._
import io.circe._
import pseudoglot.data.Place

trait PlaceJson {
  import Place.instances._

  implicit val placeHasJsonEncode:Encoder[Place] =
    Encoder.instance(a => Json.fromString(a.show))

  implicit val placeHasJsonDecode:Decoder[Place] =
    Decoder.decodeString.emap(Place.parse)
}
