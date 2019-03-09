package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import org.scalacheck.Arbitrary
import spire.random.rng.BurtleRot2

class LanguageSpec extends xenocosm.test.XenocosmFunSuite {
  import Language.instances._

  implicit val arbLanguage:Arbitrary[Language] = Arbitrary(gen.language)

  checkAll("Eq[Language]", EqTests[Language].eqv)

  test("dist.proceeds.from.seed") {
    val seed:Array[Int] = genInts.sample.get
    val lhs = Language.dist(BurtleRot2.fromSeed(seed))
    val rhs = Language.dist(BurtleRot2.fromSeed(seed))
    lhs shouldBe rhs
  }

  test("name.proceeds.from.meaning") {
    implicit val meaning:String = Arbitrary.arbString.arbitrary.sample.get
    forAll((language:Language, meaning:String) => {
      val lhs = language.nameFor(meaning)
      val rhs = language.nameFor(meaning)

      lhs shouldBe rhs
    })
  }
}
