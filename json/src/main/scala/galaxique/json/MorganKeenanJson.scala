package galaxique.json

import io.circe._
import galaxique.MorganKeenan

trait MorganKeenanJson {
  implicit val mkHasJsonEncode:Encoder[MorganKeenan] =
    Encoder.encodeChar.contramap[MorganKeenan](_.classification)

  implicit val mkHasJsonDecode:Decoder[MorganKeenan] =
    Decoder.decodeChar.emap(MorganKeenan.fromClassificationEither)
}
