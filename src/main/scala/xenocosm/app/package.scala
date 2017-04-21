package xenocosm

import scala.util.Try
import org.http4s.{Request, headers}

import xenocosm.app.data.XenocosmConfig

package object app {
  val config:XenocosmConfig = XenocosmConfig.load.toOption.get

  def getSeed(req:Request):Option[Long] =
    for {
      cookies ← req.headers.get(headers.Cookie)
      cookie ← cookies.values.filter(_.name == "seed").headOption
      seed ← Try(cookie.content.toLong).toOption
    } yield seed
}
