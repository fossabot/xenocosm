package xenocosm

import squants.space.{Length, Volume}

sealed trait XenocosmError
final case class WrappedThrowable(throwable:Throwable) extends XenocosmError
case object NoIdentitySelected extends XenocosmError
case object NoMovesRemaining extends XenocosmError
case object NoTraderSelected extends XenocosmError
case class CannotNavigate(maxNavDistance:Length) extends XenocosmError
case class NotEnoughFuel(unusedFuel:Volume) extends XenocosmError
