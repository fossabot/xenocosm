package pseudoglot
package parser

import cats.data.EitherT
import cats.implicits._
import fastparse.all._

object PhoneSeqParser extends PhoneParser {
  import data.{Phone, Transcription}

  protected lazy val phoneSeq:Parser[Seq[Phone]] =
    P(phone ~ ("::" ~ phone).rep).map({ case (head, tail) => head +: tail })

  private lazy val notation:Parser[Seq[Phone]] =
    P(Start ~ phoneSeq ~ End)

  def parse(in:String):PhoneSeqParser =
    notation.parse(in) match {
      case Parsed.Success(x, _) ⇒ Right(x)
      case failure:Parsed.Failure ⇒ Left(failure.msg)
    }

  def parse(key:String, value:String):Either[String, (List[Phone], String)] =
    parse(key).map(_.toList -> value)

  def parse(kvs:Map[String, String]):Transcription =
    EitherT(kvs.toList.map({ case (k, v) => parse(k, v) }))
      .collectRight
      .toMap
}
