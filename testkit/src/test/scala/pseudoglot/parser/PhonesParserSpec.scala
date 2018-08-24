package pseudoglot
package parser

import cats.Show
import org.scalacheck.Arbitrary
import pseudoglot.data._

class PhonesParserSpec extends xenocosm.test.XenocosmFunSuite {
  import Phones.instances._

  implicit val arb:Arbitrary[Phones] = Arbitrary(gen.phones)

  test("Phones.show.parse.isomorphism") {
    forAll { as:Phones ⇒ PhonesParser.parse(implicitly[Show[Phones]].show(as)) shouldBe Right(as) }
  }

  test("parse.failure") {
    PhonesParser.parse("") shouldBe Left("(pulmonic | vowel):1:1 ...\"\"")
  }
}
