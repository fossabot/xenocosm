package xenocosm.json
package command

import io.circe._
import spire.math.UInt

import xenocosm.CreateTrader

trait CreateTraderJson {
  import io.circe.syntax._
  import interop.spire.json.instances._

  implicit val createTraderHasJsonEncoder:Encoder[CreateTrader] =
    Encoder.instance(cmd =>
      Json.obj(
        "moves" -> cmd.moves.asJson
      )
    )

  implicit val createTraderHasJsonDecoder:Decoder[CreateTrader] =
    Decoder.instance { hcur =>
      for {
        moves <- hcur.downField("moves").as[UInt]
      } yield CreateTrader(moves)
    }

}
