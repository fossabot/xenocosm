package xenocosm

import cats.data.{EitherT, Kleisli}
import spire.random.Dist

trait CommandHandler[C <: XenocosmCommand, E <: XenocosmEvent] {
  def verification:Verification[C, E]
}

object CommandHandler {
  import interop.spire.instances._

  def apply[C <: XenocosmCommand, E <: XenocosmEvent](validate:C => Either[XenocosmError, Unit], onSuccess:C => Dist[E]):CommandHandler[C, E] =
    new CommandHandler[C, E] {
      def verification:Verification[C, E] =
        Kleisli(a => EitherT(Dist.constant(validate(a))).semiflatMap(_ => onSuccess(a)).value)
    }

  trait Syntax {
    implicit class CommandHandlerOps[C <: XenocosmCommand, E <: XenocosmEvent](underlying:C)(implicit ev:CommandHandler[C, E]) {
      def verify:EitherT[Dist, XenocosmError, E] =
        EitherT(ev.verification.run(underlying))
    }
  }
  object syntax extends Syntax
}
