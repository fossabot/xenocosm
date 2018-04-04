package xenocosm.json.hal

import galaxique.data.Point3
import io.circe.Json

trait JsonHal {
  import io.circe.syntax._

  val apiCurie:Json = Json.obj(
    "name" -> "api".asJson,
    "href" -> "https://robotsnowfall.github.io/xenocosm/docs/rest/{rel}".asJson,
    "templated" -> Json.True
  )

  def locUrls[A](base:String, f:A => Point3)(as:Iterator[A]):Seq[String] =
    as.map(a => s"$base/${f(a).x.value.toLong},${f(a).y.value.toLong},${f(a).z.value.toLong}").toSeq
}
