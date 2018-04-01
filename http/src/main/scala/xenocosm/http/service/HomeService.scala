package xenocosm.http
package service

import cats.effect.IO
import org.http4s.HttpService
import org.http4s.dsl.io._

object HomeService {
  val service = HttpService[IO] {
    case GET -> Root ⇒ Ok()
  }
}
