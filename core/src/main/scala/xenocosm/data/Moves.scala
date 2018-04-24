package xenocosm
package data

import cats.kernel.Eq

final case class Moves(remaining:Int) extends AnyVal {
  def + (rhs:Int):Moves = Moves(remaining + rhs)
  def - (rhs:Int):Moves = Moves(remaining - rhs)
}

object Moves {

  trait Instances {
    implicit val movesHasEq:Eq[Moves] = Eq.fromUniversalEquals[Moves]
  }
  object instances extends Instances
}
