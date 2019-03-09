package pseudoglot

import cats.data.NonEmptyList
import org.scalacheck.{Arbitrary, Gen}
import pseudoglot.data.{Phones, Transcription}
import spire.random.Generator
import spire.random.rng.BurtleRot2

class RomanizationSpec extends xenocosm.test.XenocosmFunSuite {
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

  test("Romanization.proceeds.from.seed") {
    implicit val arbPhones: Arbitrary[Phones] = Arbitrary(gen.phones)
    implicit val arbSeed:Arbitrary[Array[Int]] = Arbitrary(genInts)
    forAll { (seed:Array[Int], phones:Phones) ⇒
      val lhs = phones.transcribe(Romanization.dist(BurtleRot2.fromSeed(seed)))
      val rhs = phones.transcribe(Romanization.dist(BurtleRot2.fromSeed(seed)))

      lhs shouldBe rhs
    }
  }

  test("Romanization.Pulmonic.transcription") {
    val gen:Generator = spireRNG
    implicit val xscript:Transcription = Romanization.dist(gen)
    forAll { xs:NonEmptyList[Pulmonic] ⇒
      val transcription = xs.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= 1
    }
  }

  test("Romanization.Vowel.transcription") {
    val gen:Generator = spireRNG
    implicit val xscript:Transcription = Romanization.dist(gen)
    forAll { xs:NonEmptyList[Vowel] ⇒
      val transcription = xs.transcribe

      transcription shouldBe a[String]
      transcription.length should be >= 1
    }
  }
}
