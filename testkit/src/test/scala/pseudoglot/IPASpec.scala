package pseudoglot

import cats.data.NonEmptyList
import org.scalacheck.{Arbitrary, Gen}

class IPASpec extends xenocosm.test.XenocosmFunSuite {
  import IPA.instances._
  import data.{Pulmonic, Vowel}
  import data.Phones.syntax._

  implicit val arbPulmonics:Arbitrary[NonEmptyList[Pulmonic]] = Arbitrary {
    for {
      head <- gen.pulmonic
      tails <- Gen.listOf(gen.pulmonic)
    } yield NonEmptyList(head, tails)
  }

  implicit val arbVowels:Arbitrary[NonEmptyList[Vowel]] = Arbitrary {
    for {
      head <- gen.vowel
      tails <- Gen.listOf(gen.vowel)
    } yield NonEmptyList(head, tails)
  }

  test("IPA.Pulmonic.transcription") {
    forAll { xs:NonEmptyList[Pulmonic] ⇒
      val transcription = xs.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= xs.size
      if (xs.forall(IPA.pulmonics.contains)) transcription should not contain "_"
    }
  }

  test("IPA.Vowel.transcription") {
    forAll { xs:NonEmptyList[Vowel] ⇒
      val transcription = xs.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= xs.size
      if (xs.forall(IPA.vowels.contains)) transcription should not contain "_"
    }
  }
}
