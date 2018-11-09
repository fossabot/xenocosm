package xenocosm
package data

import cats.kernel.Eq

final case class ForeignID(underlying:String)

object ForeignID {
  trait Instances {
    implicit val foreignIDHasEq:Eq[ForeignID] = Eq.fromUniversalEquals[ForeignID]
  }
  object instances extends Instances
}
