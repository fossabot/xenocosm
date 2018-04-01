package pseudoglot
package parser

import cats.implicits._
import spire.random.Dist

import pseudoglot.data._

class PhoneSeqParserSpec extends xenocosm.test.XenocosmSuite {
  import PhoneSeq.instances._
  import PhoneSeq.syntax._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  private val distArr:Dist[Array[Option[Phone]]] =
    for {
      pSize <- Dist.intrange(0, pulmonics.size)
      ps <- pulmonics.distPulmonic.pack(pSize)
      vSize <- Dist.intrange(0, vowels.size)
      vs <- vowels.distVowel.pack(vSize)
    } yield ps ++ vs

  implicit private val dist:Dist[Seq[Phone]] = distArr.map(_.flatten.toSeq)

  test("PhoneSeq.show.parse.isomorphism") {
    forAll { (as:Seq[Phone]) ⇒ PhoneSeqParser.parse(as.show) shouldBe Right(as) }
  }

  test("parse.failure") {
    PhoneSeqParser.parse("") shouldBe Left("(pulmonic | vowel):1:1 ...\"\"")
  }
}
