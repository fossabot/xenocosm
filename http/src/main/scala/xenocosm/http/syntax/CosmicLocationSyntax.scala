package xenocosm.http
package syntax

import org.http4s.Uri

import xenocosm.data.CosmicLocation

trait CosmicLocationSyntax {
  implicit class CosmicLocationOps(underlying:CosmicLocation) {
    val uri:Uri = Uri.unsafeFromString("multiverse") / ⎈(underlying.uuid) /
      underlying.locU.map(✺(_)).getOrElse("") /
      underlying.locG.map(✨(_)).getOrElse("") /
      underlying.locS.map(★(_)).getOrElse("")
  }
}
