package cavernetics

import cats.data.Kleisli
import cats.implicits._

object FSM {

  def apply[A, B, C](pf:PartialFunction[(A, B), Either[C, A]]):FSM[A, B, C] =
    Kleisli { ab =>
      pf.applyOrElse[(A, B), Either[C, A]](ab, ab0 => Right(ab0._1))
    }

  trait Syntax {
    implicit class FSMOps[A, B, C](s0:A)(implicit val ev:FSM[A, B, C]) {
      def transition(event:B):Either[C, A] = ev.run((s0, event))

      def transition(events:Seq[B]):Either[C, A] =
        events.foldLeft(Either.right[C, A](s0))({
          case (s, event) => s.flatMap(_.transition(event))
        })
    }
  }
  object syntax extends Syntax
}
