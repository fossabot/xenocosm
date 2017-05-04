package xenocosm
package app
package screen

object Multiverse {

  val poem:String =
    """Once,
      |When you were a eight,
      |You climbed that chestnut tree --
      |The one with the prickly fruit --
      |Until you could see
      |Above your house.
      |
      |Now,
      |You collect universes --
      |So many houses
      |Of whole good memories,
      |Empty now
      |And colored like bone.
      |""".stripMargin

  // scalastyle:off magic.number
  val screen:fansi.Str =
    fansi.Color.True(0, 255, 255)(poem).
      overlay(fansi.Color.White, 227, 236)
  // scalastyle:on magic.number
}
