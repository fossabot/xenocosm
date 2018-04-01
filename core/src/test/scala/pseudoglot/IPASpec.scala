package pseudoglot

import spire.random.Dist

class IPASpec extends xenocosm.test.XenocosmSuite {
  import data.Phone.instances._
  import data.{Pulmonic, Vowel}
  import data.PhoneSeq.syntax._
  import IPA.instances._

  implicit val pulmonics:Dist[Seq[Pulmonic]] = Dist[Pulmonic].pack(10).map(_.toSeq)
  implicit val vowels:Dist[Seq[Vowel]] = Dist[Vowel].pack(10).map(_.toSeq)

  test("IPA.Pulmonic.transcription") {
    forAll { (as:Seq[Pulmonic]) ⇒
      val transcription = as.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= as.size
      if (as.forall(IPA.pulmonics.contains)) transcription should not contain "_"
    }
  }

  test("IPA.Vowel.transcription") {
    forAll { (as:Seq[Vowel]) ⇒
      val transcription = as.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= as.size
      if (as.forall(IPA.vowels.contains)) transcription should not contain "_"
    }
  }
}
