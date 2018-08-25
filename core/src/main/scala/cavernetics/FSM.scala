package cavernetics

import cats.Eval
import cats.data.Kleisli

object FSM {

  def apply[S, E](pf:PartialFunction[(S, E), S]):FSM[S, E] =
    Kleisli.apply(te => Eval.now(pf.applyOrElse[(S, E), S](te, _._1)))

  trait Syntax {
    implicit class FSMOps[S, E](underlying:S)(implicit val ev:FSM[S, E]) {
      def transition(event:E):Eval[S] = ev.run((underlying, event))
      def transition(events:Seq[E]):Eval[S] = events.foldLeft(Eval.now(underlying))({ case (et, event) => et.flatMap(_.transition(event)) })
    }
  }
  object syntax extends Syntax
}
