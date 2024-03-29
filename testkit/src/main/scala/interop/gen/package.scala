package interop

import _root_.squants.space.{Length, Meters}
import org.scalacheck.Gen

package object gen {
  lazy val length:Gen[Length] = Gen.chooseNum(Double.MinValue, Double.MaxValue).map(Meters.apply[Double])
}
