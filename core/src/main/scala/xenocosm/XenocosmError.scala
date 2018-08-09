package xenocosm

import squants.space.Length

sealed trait XenocosmError
case object NoMovesRemaining extends XenocosmError
case class TooFar(distance:Length) extends XenocosmError
