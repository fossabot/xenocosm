package xenocosm
package data

import java.util.UUID
import cats.kernel.Eq
import spire.math.UInt

final case class Identity(uuid:UUID, ref:Option[ForeignID], moves:UInt)

object Identity {
  trait Instances {
    implicit val identityHasEq:Eq[Identity] = Eq.fromUniversalEquals[Identity]
  }
  object instances extends Instances
}
