package pseudoglot
package data

import cats.{Eq, Show}
import cats.implicits._

object Phones {
  import Phone.instances._

  val stringify:Phones => String =
    _.foldRight(List.empty[String])({
      case (NullPhoneme, ys) => ys
      case (x:Pulmonic, ys) => x.show :: ys
      case (x:Vowel, ys) => x.show :: ys
    }).mkString("::")

  trait Instances {
    implicit val phonesHaveEq:Eq[Phones] = Eq.fromUniversalEquals[Phones]
    implicit val phonesHaveShow:Show[Phones] = Show.show(stringify)
  }
  object instances extends Instances

  trait Syntax {
    implicit class PhonesOps(underlying:Phones) {
      lazy val vowels:List[Vowel] = underlying collect { case x:Vowel => x }
      lazy val pulmonics:List[Pulmonic] = underlying collect { case x:Pulmonic => x }

      def transcribe(implicit xscript:Transcription):String =
        Transcription.transcribeWith(underlying, xscript)
    }
  }
  object syntax extends Syntax
}
