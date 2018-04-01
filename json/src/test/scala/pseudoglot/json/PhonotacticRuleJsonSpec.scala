package pseudoglot.json

import cats.implicits._
import io.circe.syntax._
import spire.random.Dist
import pseudoglot.IPA
import pseudoglot.data.{PhoneSeq, PhonotacticRule}

class PhonotacticRuleJsonSpec extends xenocosm.test.XenocosmSuite {
  import PhoneSeq.syntax._
  import PhonotacticRule.instances._
  import phonotacticRule._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  implicit private val dist:Dist[PhonotacticRule] =
    for {
      p <- pulmonics.distRule
      v <- vowels.distRule
    } yield p |+| v

  test("PhonotacticRule.json.isomorphism") {
    forAll { (a:PhonotacticRule) =>
      a.asJson.as[PhonotacticRule] shouldBe Right(a)
    }
  }
}
