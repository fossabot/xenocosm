package xenocosm
package app
package screen

object Multiverse {

  val poem:String =
    """:: Once,
      |:: When you were a eight,
      |:: You climbed that chestnut tree --
      |:: The one with the prickly fruit --
      |:: Until you could see
      |:: Above your house.
      |::
      |:: Now,
      |:: You collect universes --
      |:: So many houses
      |:: Of whole good memories,
      |:: Empty now
      |:: And colored like bone.
      |""".stripMargin

  // scalastyle:off magic.number
  def colorize(c:Char):fansi.Str = fansi.Color.True(0, 255, 255)(s"$c")
  def colorize(str:String):fansi.Str = fansi.Color.True(0, 255, 255)(str)
  // scalastyle:on magic.number

  private val highlight:String ⇒ fansi.Str = section ⇒
    poem.split("\n").
      map({
        case line if line contains section ⇒
          val start = line indexOf section
          val end = start + section.length
          line.zipWithIndex.map({
            case (c, i) if i >= start && i < end ⇒ fansi.Color.White(s"$c")
            case (c, _) ⇒ colorize(c)
          }).mkString
        case line => colorize(line)
      }).
      mkString("\n")

  def apply:fansi.Str = highlight("colored like bone") ++ "\n"
}
