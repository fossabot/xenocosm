package xenocosm
package app
package screen

import squants.space.Parsecs

object Universe {

  val show:xenocosm.universe.data.Universe ⇒ String = universe ⇒
    """A Universe
      |  Age: %e yrs
      |  Diameter: %s
      |""".stripMargin.format(
      universe.age.toDouble,
      universe.diameter.toString(Parsecs, "%e")
    )

  // scalastyle:off magic.number
  def apply(universe:xenocosm.universe.data.Universe):fansi.Str =
    fansi.Color.True(16, 255 - 16, 255)(show(universe))
  // scalastyle:on magic.number
}
