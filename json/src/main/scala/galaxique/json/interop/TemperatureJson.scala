package galaxique.json
package interop

import io.circe._
import squants.thermal.Temperature

trait TemperatureJson {
  implicit val temperatureHasJsonEncode:Encoder[Temperature] =
    Encoder.encodeString.contramap[Temperature](_.toString())

  implicit val temperatureHasJsonDecode:Decoder[Temperature] =
    Decoder.decodeString.emapTry(Temperature.apply)
}
