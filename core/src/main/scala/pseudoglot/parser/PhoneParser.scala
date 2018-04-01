package pseudoglot
package parser

import cats.implicits._
import fastparse.all._
import pseudoglot.data._

trait PhoneParser {
  import Backness.instances._
  import Height.instances._
  import Manner.instances._
  import Place.instances._
  import Roundedness.instances._
  import Voicing.instances._

  protected lazy val voicing:Parser[Voicing] =
    P(StringIn(Voicing.all.map(_.show):_*).!)
      .map(Voicing.parse)
      .map(_.toOption.get)

  protected lazy val place:Parser[Place] =
    P(StringIn(Place.all.map(_.show):_*).!)
      .map(Place.parse)
      .map(_.toOption.get)

  protected lazy val manner:Parser[Manner] =
    P(StringIn(Manner.all.map(_.show):_*).!)
      .map(Manner.parse)
      .map(_.toOption.get)

  protected lazy val pulmonic:Parser[Pulmonic] =
    P(voicing ~ ":" ~ place ~ ":" ~ manner)
      .map(Pulmonic.tupled)

  protected lazy val roundedness:Parser[Roundedness] =
    P(StringIn(Roundedness.all.map(_.show):_*).!)
      .map(Roundedness.parse)
      .map(_.toOption.get)

  protected lazy val height:Parser[Height] =
    P(StringIn(Height.all.map(_.show):_*).!)
      .map(Height.parse)
      .map(_.toOption.get)

  protected lazy val backness:Parser[Backness] =
    P(StringIn(Backness.all.map(_.show):_*).!)
      .map(Backness.parse)
      .map(_.toOption.get)

  protected lazy val vowel:Parser[Vowel] =
    P(roundedness ~ ":" ~ height ~ ":" ~ backness)
      .map(Vowel.tupled)

  protected lazy val phone:Parser[Phone] =
    P(pulmonic | vowel)

}
