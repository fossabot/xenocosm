package cavernetics

import cats.Eval
import cats.data.Kleisli

object FSM {

  def apply[T, E](pf:PartialFunction[(T, E), T]):FSM[T, E] =
    Kleisli.apply(te => Eval.now(pf.applyOrElse[(T, E), T](te, _._1)))

  trait Syntax {
    implicit class FSMOps[T, E](underlying:T)(implicit val ev:FSM[T, E]) {
      def transition(event:E):Eval[T] = ev.run((underlying, event))
      def transition(events:Seq[E]):Eval[T] = events.foldLeft(Eval.now(underlying))({ case (et, event) => et.flatMap(_.transition(event)) })
    }
  }
  object syntax extends Syntax
}
