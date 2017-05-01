package xenocosm
package app
package screen

object Home {

  private val title:String =
    """################################################################
      |################################################################
      |#####     _  _____  ____  ____  _________  _________ ___   #####
      |#####    | |/_/ _ \/ __ \/ __ \/ ___/ __ \/ ___/ __ `__ \  #####
      |#####   _>  </  __/ / / / /_/ / /__/ /_/ (__  ) / / / / /  #####
      |#####  /_/|_|\___/_/ /_/\____/\___/\____/____/_/ /_/ /_/   #####
      |################################################################
      |################################################################""".stripMargin

  private val blurb:String =
    """Where have You hidden Yourself,
      |And abandoned me in my groaning, O my Beloved?
      |You have fled like the hart,
      |Having wounded me.
      |I ran after You, crying; but You were gone.
      |
      |St. John of the Cross, "A Spiritual Canticle", ca. 01578""".stripMargin

  // scalastyle:off magic.number
  private val rainbow:fansi.Str =
    title.
      split("\n").
      map(_.
        zipWithIndex.
        map({ case (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c") }).
        mkString).
      mkString("\n")

  private val highlight:fansi.Str =
    fansi.Color.White(blurb).overlay(
      fansi.Color.True(255, 0, 255),
      blurb.length - 72,
      blurb.length - 59
    )
  // scalastyle:on magic.number

  val screen:fansi.Str =
    rainbow ++ "\n\n" ++ highlight
}
