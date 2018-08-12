package pseudoglot
package data

import cats.implicits._
import cats.kernel.laws.discipline.MonoidTests
import spire.random.Dist

class TranscriptionSpec extends xenocosm.test.XenocosmSuite {
  import Phone.instances._
  import Phones.syntax._
  import Transcription.instances._

  private val distPhone:Dist[Phone] =
    for {
      pulmonic <- Dist[Pulmonic]
      vowel <- Dist[Vowel]
      phone <- Dist.oneOf(pulmonic, vowel)
    } yield phone

  private val distTransript:Dist[(Phones, String)] =
    for {
      phones <- distPhone.pack(8).map(_.toList)
      xscript <- Dist.char.pack(8).map(_.mkString)
    } yield phones -> xscript

  implicit private val dist:Dist[Transcription] =
    distTransript.pack(8).map(_.toMap)

  checkAll("Monoid[Transcription]", MonoidTests[Transcription].monoid)

  test("null sequence") {
    import IPA.instances._
    val nullSeq:Phones = List(NullPhoneme)
    nullSeq.transcribe shouldBe ""
  }
}
