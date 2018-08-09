package xenocosm.http

import io.circe.{Decoder, HCursor, Json}
import org.http4s.dsl.impl.Path

package object response {
  import io.circe.syntax._

  def selfPath(hcursor:HCursor):Decoder.Result[Path] =
    hcursor
      .downField("_links")
      .downField("self")
      .downField("href")
      .as[String]
      .map(Path.apply)

  val apiCurie:Json = Json.obj(
    "name" -> "api".asJson,
    "href" -> "https://robotsnowfall.github.io/xenocosm/docs/rest/{rel}".asJson,
    "templated" -> Json.True
  )
}
