package pseudoglot
package parser

import cats.data.EitherT
import cats.implicits._
import fastparse.all._

object PhonesParser extends PhoneParser {
  import data.{Phones, Transcription}

  protected lazy val phoneSeq:Parser[Phones] =
    P(phone ~ ("::" ~ phone).rep).map({ case (head, tail) => (head +: tail).toList })

  private lazy val notation:Parser[Phones] =
    P(Start ~ phoneSeq ~ End)

  def parse(in:String):PhonesParser =
    notation.parse(in) match {
      case Parsed.Success(x, _) ⇒ Right(x)
      case failure:Parsed.Failure ⇒ Left(failure.msg)
    }

  def parse(key:String, value:String):Either[String, (Phones, String)] =
    parse(key).map(_ -> value)

  def parse(kvs:Map[String, String]):Transcription =
    EitherT(kvs.toList.map({ case (k, v) => parse(k, v) }))
      .collectRight
      .toMap
}
