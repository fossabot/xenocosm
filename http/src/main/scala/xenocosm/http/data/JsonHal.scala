package xenocosm.http
package data

import io.circe.{Decoder, HCursor, Json}
import org.http4s.dsl.impl._

trait JsonHal[A] {
  import io.circe.syntax._

  def selfPath(hcursor:HCursor):Decoder.Result[Path] =
    hcursor
      .downField("_links")
      .downField("self")
      .downField("href")
      .as[String]
      .map(Path.apply)

  def cleanBase(base:A):Json
  def baseFromSelfLink(hcursor:HCursor):Decoder.Result[A]

  val apiCurie:Json = Json.obj(
    "name" -> "api".asJson,
    "href" -> "https://robotsnowfall.github.io/xenocosm/docs/rest/{rel}".asJson,
    "templated" -> Json.True
  )
}
