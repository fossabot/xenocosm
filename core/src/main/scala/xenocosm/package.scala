import cats.data.Kleisli
import spire.random.Dist

package object xenocosm {
  // A type alias for the output of a `CommandHandler.verify`
  type Verification[C <: XenocosmCommand, E <: XenocosmEvent] = Kleisli[Dist, C, Either[XenocosmError, E]]
}
