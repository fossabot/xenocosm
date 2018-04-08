package xenocosm.http.service

import org.http4s.{Charset, MediaType}
import org.http4s.headers.`Content-Type`

trait XenocosmAPI {
  val jsonHal = `Content-Type`(MediaType.`application/hal+json`, Charset.`UTF-8`)
}
