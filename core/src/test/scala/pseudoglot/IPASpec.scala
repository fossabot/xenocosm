package pseudoglot

import spire.random.Dist

class IPASpec extends xenocosm.test.XenocosmFunSuite {
  import IPA.instances._
  import data.Phone.instances._
  import data.{Pulmonic, Vowel}
  import data.Phones.syntax._

  implicit val pulmonics:Dist[List[Pulmonic]] = Dist[Pulmonic].pack(10).map(_.toList)
  implicit val vowels:Dist[List[Vowel]] = Dist[Vowel].pack(10).map(_.toList)

  test("IPA.Pulmonic.transcription") {
    forAll { xs:List[Pulmonic] ⇒
      val transcription = xs.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= xs.size
      if (xs.forall(IPA.pulmonics.contains)) transcription should not contain "_"
    }
  }

  test("IPA.Vowel.transcription") {
    forAll { xs:List[Vowel] ⇒
      val transcription = xs.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= xs.size
      if (xs.forall(IPA.vowels.contains)) transcription should not contain "_"
    }
  }
}
