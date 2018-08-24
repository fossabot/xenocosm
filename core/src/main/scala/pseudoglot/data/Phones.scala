package pseudoglot
package data

import cats.{Eq, Eval, Show}
import cats.implicits._

object Phones {
  import Phone.instances._

  val stringify:Phones => String =
    _.foldRight(Eval.now(List.empty[String]))({
      case (NullPhoneme, ys) => ys
      case (x:Pulmonic, ys) => ys.map(x.show :: _)
      case (x:Vowel, ys) => ys.map(x.show :: _)
    }).value.mkString("::")

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
