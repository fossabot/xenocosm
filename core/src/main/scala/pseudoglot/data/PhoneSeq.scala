package pseudoglot
package data

import scala.annotation.tailrec
import cats.{Eq, Show}
import cats.implicits._
import spire.random.Dist

object PhoneSeq {
  import Phone.instances._

  private def stringify(xs:Seq[Phone]):String =
    xs.foldRight(List.empty[String])({
      case (NullPhoneme, ys) => ys
      case (x:Pulmonic, ys) => x.show :: ys
      case (x:Vowel, ys) => x.show :: ys
    }).mkString("::")

  trait Instances {
    implicit val phoneSeqHasEq:Eq[Seq[Phone]] = Eq.fromUniversalEquals[Seq[Phone]]
    implicit val phoneSeqHasShow:Show[Seq[Phone]] = Show.show(stringify)
    implicit val phoneListHasShow:Show[List[Phone]] = Show.show(stringify)
  }
  object instances extends Instances

  trait Syntax {
    implicit class PhoneSeqOps(underlying:Seq[Phone]) {
      lazy val vowels:List[Vowel] =
        underlying.foldRight(List.empty[Vowel])({
          case (x:Vowel, xs) => x :: xs
          case (_, xs) => xs
        })

      lazy val pulmonics:List[Pulmonic] =
        underlying.foldRight(List.empty[Pulmonic])({
          case (x:Pulmonic, xs) => x :: xs
          case (_, xs) => xs
        })

      def distVowel:Dist[Option[Vowel]] =
        vowels match {
          case Nil => Dist.constant(None)
          case xs => Dist.oneOf(xs:_*).map(Some.apply)
        }

      def distPulmonic:Dist[Option[Pulmonic]] =
        pulmonics match {
          case Nil => Dist.constant(None)
          case xs => Dist.oneOf(xs:_*).map(Some.apply)
        }

      // scalastyle:off cyclomatic.complexity
      def distRule:Dist[PhonotacticRule] = Dist.gen { gen ⇒
        def literalPulmonic:PhonotacticRule = distPulmonic(gen).map(Literal.apply).getOrElse(Empty)
        def literalVowel:PhonotacticRule = distVowel(gen).map(Literal.apply).getOrElse(Empty)

        @tailrec
        def loop(acc:PhonotacticRule):PhonotacticRule = (acc, gen.nextDouble()) match {
          case (Empty, prob) if prob > 0.2 ⇒ loop(gen.oneOf(AnyPulmonic, AnyVowel))
          case (AnyVowel, prob) if prob > 0.3 ⇒ loop(literalVowel)
          case (Literal(x:Vowel), prob) if prob > 0.4 ⇒ loop(Choose(List(literalVowel, Literal(x))))
          case (AnyPulmonic, prob) if prob > 0.3 ⇒ loop(literalPulmonic)
          case (Literal(x:Pulmonic), prob) if prob > 0.4 ⇒ loop(Choose(List(literalPulmonic, Literal(x))))
          case (Choose(xs), prob) if prob > 0.5 ⇒ Concat(xs)
          case _ ⇒ acc
        }

        loop(Empty)
      }
      // scalastyle:on cyclomatic.complexity
      def transcribe(implicit xscript:Transcription):String =
        Transcription.transcribeWith(underlying.toList, xscript)
    }
  }
  object syntax extends Syntax
}
