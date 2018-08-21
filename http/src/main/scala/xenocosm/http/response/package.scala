package xenocosm.http

import io.circe.Json

package object response {
  import io.circe.syntax._

  val apiCurie:Json = Json.obj(
    "name" -> "api".asJson,
    "href" -> "https://robotsnowfall.github.io/xenocosm/docs/rest/{rel}".asJson,
    "templated" -> Json.True
  )
}
