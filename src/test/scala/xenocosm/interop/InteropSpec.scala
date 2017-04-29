package xenocosm
package interop

import cats.kernel.laws.OrderLaws
import spire.laws.GroupLaws
import squants.energy.Power
import squants.mass.Mass
import squants.space.Length
import squants.thermal.Temperature

class InteropSpec extends XenocosmSuite {
  import instances._

  checkAll("Order[Length]", OrderLaws[Length].order)
  checkAll("AdditiveGroup[Length]", GroupLaws[Length].additiveGroup)
  checkAll("Order[Mass]", OrderLaws[Mass].order)
  checkAll("AdditiveGroup[Mass]", GroupLaws[Mass].additiveGroup)
  checkAll("Order[Power]", OrderLaws[Power].order)
  checkAll("AdditiveGroup[Power]", GroupLaws[Power].additiveGroup)
  checkAll("Order[Temperature]", OrderLaws[Temperature].order)
  checkAll("AdditiveGroup[Temperature]", GroupLaws[Temperature].additiveGroup)
}
