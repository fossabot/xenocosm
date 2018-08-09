package interop.squants.json

import io.circe.{Decoder, Encoder}
import squants.motion.VolumeFlow

trait VolumeFlowJson {
  implicit val volumeFlowHasJsonEncode:Encoder[VolumeFlow] =
    Encoder.encodeString.contramap[VolumeFlow](_.toString())

  implicit val volumeFlowHasJsonDecode:Decoder[VolumeFlow] =
    Decoder.decodeString.emapTry(VolumeFlow.apply)
}
