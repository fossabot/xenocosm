package pseudoglot
package data

import cats.implicits._
import cats.kernel.laws.discipline.MonoidTests
import spire.random.Dist

class TranscriptionSpec extends xenocosm.test.XenocosmSuite {
  import PhoneSeq.syntax._
  import Transcription.instances._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  private val distArr:Dist[Array[(List[Phone], String)]] =
    for {
      pSize <- Dist.intrange(0, pulmonics.size)
      ps <- pulmonics.distPulmonic.map(x => List(x.get) -> IPA.pulmonics(x.get)).pack(pSize)
      vSize <- Dist.intrange(0, vowels.size)
      vs <- vowels.distVowel.map(x => List(x.get) -> IPA.vowels(x.get)).pack(vSize)
    } yield ps ++ vs
  implicit private val dist:Dist[Transcription] = distArr.map(_.toMap)

  checkAll("Monoid[Transcription]", MonoidTests[Transcription].monoid)

  test("null sequence") {
    import IPA.instances._
    val nullSeq:Seq[Phone] = Seq(NullPhoneme)
    nullSeq.transcribe shouldBe ""
  }
}
