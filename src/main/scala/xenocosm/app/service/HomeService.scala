package xenocosm
package app
package service

import scala.util.Random
import org.http4s._
import org.http4s.dsl._

import xenocosm.interop.instances._

object HomeService {

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
    """
      |
      |Where have You hidden Yourself,
      |And abandoned me in my groaning, O my Beloved?
      |You have fled like the hart,
      |Having wounded me.
      |I ran after You, crying; but You were gone.
      |
      |St. John of the Cross, "A Spiritual Canticle", ca. 01578""".stripMargin

  val screen:fansi.Str =
    title.
      split("\n").
      map(_.
        zipWithIndex.
        map({ case (c, i) ⇒ fansi.Color.True(i * 4, 255 - (i * 4), 255)(s"$c") }).
        mkString).
      mkString("\n")

  val service = HttpService {
    case req @ GET -> Root ⇒
      getSeed(req) match {
        case Some(_) ⇒
          Ok(screen ++ fansi.Color.White(blurb)).
            putHeaders(MultiverseService.location(req))
        case None ⇒
          val seed = Random.nextLong()
          Ok(screen ++ fansi.Color.White(blurb)).
            addCookie(Cookie("seed", seed.toString)).
            putHeaders(MultiverseService.location(req))
      }
  }
}
