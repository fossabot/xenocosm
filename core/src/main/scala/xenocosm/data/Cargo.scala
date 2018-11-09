package xenocosm
package data

import cats.kernel.Eq

sealed trait Cargo
case object Vacuum extends Cargo

object Cargo {
  trait Instances {
    implicit val cargoHasEq:Eq[Cargo] = Eq.fromUniversalEquals[Cargo]
  }
  object instances extends Instances
}
