package xenocosm.http

import io.circe.Json

package object response {
  import io.circe.syntax._

  val apiCurie:Json = Json.obj(
    "name" -> "api".asJson,
    "href" -> "https://xenocosm.docs.apiary.io/#reference/0/{rel}".asJson,
    "templated" -> Json.True
  )
}
