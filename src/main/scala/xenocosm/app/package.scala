package xenocosm

import java.util.UUID
import scala.util.Try
import org.http4s.{headers, Request}

import xenocosm.app.data.XenocosmConfig
import xenocosm.phonology.Romanization
import xenocosm.universe.data.Universe

package object app {
  val config:XenocosmConfig = XenocosmConfig.load.get
  implicit val romanization:Romanization = Romanization.enUS(config)

  def getSeed(req:Request):Option[Long] =
    for {
      cookies ← req.headers.get(headers.Cookie)
      cookie ← cookies.values.filter(_.name == "seed").headOption
      seed ← Try(cookie.content.toLong).toOption
    } yield seed

  object UniverseVal {
    def unapply(str:String):Option[Universe] =
      Try(UUID.fromString(str)).toOption.map(Universe.apply)
  }
  val ♈ = UniverseVal
  val ♉ = service.IntergalacticCoordinateService.Point3Val
  val ♊ = service.InterstellarCoordinateService.Point3Val
  val ♋ = service.InterplanetaryCoordinateService.Point3Val
}
