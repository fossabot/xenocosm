package xenocosm.http
package syntax

import org.http4s.Uri

import xenocosm.data.Trader

trait TraderSyntax {
  implicit class TraderOps(underlying:Trader) {
    val uri:Uri = apiTrader / ⎈(underlying.uuid)
  }
}
