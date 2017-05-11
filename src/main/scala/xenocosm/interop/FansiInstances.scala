package xenocosm
package interop

import cats.Show
import org.http4s.{Charset, EntityEncoder}
import fansi.Str

object FansiInstances {
  trait Instances {
    implicit val fansiStrShow:Show[Str] = Show.show(_.plainText)
    implicit val fansiStrEntityEncoder:EntityEncoder[Str] =
      EntityEncoder.showEncoder[Str](Charset.`UTF-8`, fansiStrShow)
  }
  object instances extends Instances
}
