package pseudoglot
package data

import cats.data.NonEmptyList
import cats.implicits._
import cats.kernel.laws.discipline.MonoidTests
import org.scalacheck.Arbitrary

class TranscriptionSpec extends xenocosm.test.XenocosmFunSuite {
  import Phones.syntax._
  import Transcription.instances._

  implicit val arb:Arbitrary[Transcription] = Arbitrary(gen.transcription)

  checkAll("Monoid[Transcription]", MonoidTests[Transcription].monoid)

  test("null sequence") {
    import IPA.instances._
    val nullSeq:Phones = NonEmptyList(NullPhoneme, Nil)
    nullSeq.transcribe shouldBe ""
  }
}
