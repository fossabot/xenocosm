package economancy

trait Economy[T]

object Economy {
  trait Syntax {
    implicit class EconomyOps[T](underlying:T)(implicit ev:Economy[T]) {

    }
  }
  object syntax extends Syntax
}
