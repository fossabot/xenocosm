package xenocosm.http
package data

import galaxique.data.{Galaxy, Point3, Universe}
import galaxique.implicits._
import squants.space.Length

final case class UniverseResponse(universe:Universe, loc:Point3, range:Length) {
  lazy val galaxies:Iterator[Galaxy] = universe.nearby(loc, range)
}
