package xenocosm.json

import io.circe.Encoder
import squants.space.{CubicMeters, LightYears}

import xenocosm._

trait XenocosmErrorJson {
  import io.circe.syntax._

  implicit val xenocosmErrorHasJsonEncoder:Encoder[XenocosmError] =
    Encoder.instance({
      case WrappedThrowable(_) => "Unexpected error :-/".asJson
      case NoIdentitySelected => "No identity selected.".asJson
      case NoTraderSelected => "No trader selected.".asJson
      case NoMovesRemaining => "No moves remaining.".asJson
      case CannotNavigate(maxNavDistance) =>
        s"Cannot navigate to location. Maximum distance is ${maxNavDistance.in(LightYears)}".asJson
      case NotEnoughFuel(unusedFuel) =>
        s"Not enough fuel. Remaining fuel is ${unusedFuel.in(CubicMeters)}".asJson
    })
}
