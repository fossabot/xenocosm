package pseudoglot
package parser

import cats.Eq
import fastparse.all._

object PhonotacticParser extends PhoneParser {
  import data._

  private lazy val literalPulmonic:Parser[Literal[Pulmonic]] =
    P("[" ~ pulmonic ~ "]").map(Literal.apply)

  private lazy val anyPulmonic:Parser[PhonotacticRule] =
    P("P").map(_ ⇒ AnyPulmonic)

  private lazy val literalVowel:Parser[Literal[Vowel]] =
    P("[" ~ vowel ~ "]").map(Literal.apply)

  private lazy val anyVowel:Parser[PhonotacticRule] =
    P("V").map(_ ⇒ AnyVowel)

  private lazy val phoneRule:Parser[PhonotacticRule] =
    P(anyPulmonic | anyVowel | literalPulmonic | literalVowel)

  private val toConcat:List[PhonotacticRule] => PhonotacticRule = {
    case Nil => Empty
    case x :: Nil => x
    case x :: y :: Nil => Concat(x, y)
    case x :: ys => Concat(x, toConcat(ys))
  }

  private lazy val concat:Parser[PhonotacticRule] =
    P("(" ~ rules.rep ~ ")").map(xs => toConcat(xs.toList))

  private val toChoose:List[PhonotacticRule] => PhonotacticRule = {
    case Nil => Empty
    case x :: Nil => x
    case x :: y :: Nil => Choose(x, y)
    case x :: ys => Choose(x, toChoose(ys))
  }

  private lazy val choose:Parser[PhonotacticRule] =
    P("(" ~ rules ~ ("|" ~ rules).rep.? ~ ")")
      .map(t => t._1 :: t._2.getOrElse(Seq.empty[PhonotacticRule]).toList)
      .map(toChoose)

  private lazy val rules:Parser[PhonotacticRule] =
    P(phoneRule | concat | choose)

  private lazy val notation:Parser[PhonotacticRule] =
    P(Start ~ rules.? ~ End).map({
      case Some(rule) => rule
      case None => Empty
    })

  def parse(in:String):PhonotacticParser =
    notation.parse(in) match {
      case Parsed.Success(x, _) ⇒ Right(x)
      case failure:Parsed.Failure ⇒ Left(failure.msg)
    }

  trait Instances {
    implicit val phonotacticRuleNotationHasEq:Eq[PhonotacticParser] =
      Eq.fromUniversalEquals[PhonotacticParser]
  }
  object instances extends Instances

  trait Syntax {
    implicit class StringOps(underlying:String) {
      def asPhonotactics:PhonotacticParser = PhonotacticParser.parse(underlying)
    }
  }
  object syntax extends Syntax
}
