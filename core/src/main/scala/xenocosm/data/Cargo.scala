package xenocosm
package data

import spire.random.Dist

sealed trait Cargo
case object Vacuum extends Cargo

object Cargo {
  trait Instances {
    implicit val cargoHasDist:Dist[Cargo] = Dist.constant(Vacuum)
  }
  object instances extends Instances
}
