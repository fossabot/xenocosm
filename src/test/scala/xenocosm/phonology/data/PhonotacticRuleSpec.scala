package xenocosm
package phonology
package data

import cats.kernel.laws.OrderLaws
import spire.random.Dist

class PhonotacticRuleSpec extends XenocosmSuite {
  import PhonotacticRule.instances._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  implicit val dist:Dist[PhonotacticRule] = PhonotacticRule.dist(pulmonics, vowels)

  checkAll("Eq[PhonotacticRule]", OrderLaws[PhonotacticRule].eqv)
}
