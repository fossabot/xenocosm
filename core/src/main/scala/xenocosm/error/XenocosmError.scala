package xenocosm
package error

sealed trait XenocosmError
case object NoMovesRemaining extends XenocosmError
