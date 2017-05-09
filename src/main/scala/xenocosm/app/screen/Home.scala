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
      |St. John of the Cross, "A Spiritual Canticle", ca. 01578
      |""".stripMargin

  // scalastyle:off magic.number
  private val rainbow:(Char, Int) ⇒ fansi.Str = (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c")
  // scalastyle:on magic.number

  private val splash:fansi.Str =
    title.
      split("\n").
      map(_.zipWithIndex.map(rainbow.tupled).mkString).
      mkString("\n")

  private val highlight:String ⇒ fansi.Str = section ⇒
    blurb.split("\n").
      map({
        case line if line contains section ⇒
          val start = line indexOf section
          val end = start + section.length
          line.zipWithIndex.map({
            case (c, i) if i >= start && i < end ⇒ rainbow(c, i)
            case (c, _) ⇒ fansi.Color.White(s"$c")
          }).mkString
        case line => fansi.Color.White(line)
      }).
      mkString("\n")

  val screen:fansi.Str = splash ++ "\n\n" ++ highlight("You were gone") + "\n"
}
