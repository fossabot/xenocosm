package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.motion.Velocity

trait VelocityJson {
  implicit val velocityHasJsonEncode:Encoder[Velocity] =
    Encoder.encodeString.contramap[Velocity](_.toString())

  implicit val velocityHasJsonDecode:Decoder[Velocity] =
    Decoder.decodeString.emapTry(Velocity.apply)
}
