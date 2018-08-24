import cats.Eval
import cats.data.Kleisli

/**
  * <img src="/xenocosm/img/cavernetics-128.png" />
  * Provides data types and typeclasses for rogue-like elements such as NPCs.
  *
  * ==Overview==
  * TODO
  */
package object cavernetics {
  type FSM[T, E] = Kleisli[Eval, (T, E), T]
}
