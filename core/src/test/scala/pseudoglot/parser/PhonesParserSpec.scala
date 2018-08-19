package pseudoglot
package parser

import cats.implicits._
import spire.random.Dist

import pseudoglot.data._

class PhonesParserSpec extends xenocosm.test.XenocosmFunSuite {
  import Phone.instances._
  import Phones.instances._

  private val distPhone:Dist[Phone] =
    for {
      pulmonic <- Dist[Pulmonic]
      vowel <- Dist[Vowel]
      phone <- Dist.oneOf(pulmonic, vowel)
    } yield phone
  private implicit val dist:Dist[Phones] = distPhone.pack(8).map(_.toList)

  test("PhoneSeq.show.parse.isomorphism") {
    forAll { as:Phones ⇒ PhonesParser.parse(as.show) shouldBe Right(as) }
  }

  test("parse.failure") {
    PhonesParser.parse("") shouldBe Left("(pulmonic | vowel):1:1 ...\"\"")
  }
}
