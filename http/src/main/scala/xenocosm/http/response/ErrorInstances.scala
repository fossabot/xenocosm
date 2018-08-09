package xenocosm.http
package response

import io.circe._
import org.http4s.dsl.impl.Path
import squants.space.Length

import xenocosm.{NoMovesRemaining, TooFar}

object ErrorInstances {
  import io.circe.syntax._
  import interop.squants.json.instances._

  trait NoMovesRemainingInstances {

    private def applyF(path:Path):Decoder.Result[NoMovesRemaining.type] =
      path.toString match {
        case "/v1/error/no-moves-remaining" => Right(NoMovesRemaining)
        case _ => Left(DecodingFailure("unrecognized response type", List.empty[CursorOp]))
      }

    implicit val errNoMovesRemainingHasJsonHalEncoder: Encoder[NoMovesRemaining.type] =
      Encoder.instance(_ => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> "/v1/error/no-moves-remaining".asJson),
          "curies" -> Json.arr(apiCurie)
        ),
        "error" -> "no moves remaining".asJson
      ))

    implicit val errNoMovesRemainingHasJsonHalDecoder:Decoder[NoMovesRemaining.type] =
      Decoder.instance { hcur =>
        for {
          path <- selfPath(hcur)
          f <- applyF(path)
        } yield f
      }
  }

  trait TooFarInstances {

    private def applyF(path:Path):Decoder.Result[Length => TooFar] =
      path.toString match {
        case "/v1/error/too-far" => Right(TooFar.apply)
        case _ => Left(DecodingFailure("unrecognized response type", List.empty[CursorOp]))
      }

    implicit val errTooFarHasJsonHalEncoder: Encoder[TooFar] =
      Encoder.instance(_ => Json.obj(
        "_links" -> Json.obj(
          "self" -> Json.obj("href" -> "/v1/error/too-far".asJson),
          "curies" -> Json.arr(apiCurie)
        ),
        "error" -> "too far".asJson
      ))

    implicit val errTooFarHasJsonHalDecoder:Decoder[TooFar] =
      Decoder.instance { hcur =>
        for {
          path <- selfPath(hcur)
          f <- applyF(path)
          length <- hcur.downField("length").as[Length]
        } yield f(length)
      }
  }

  object instances extends NoMovesRemainingInstances
                      with TooFarInstances
}
