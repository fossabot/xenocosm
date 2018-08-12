package pseudoglot
package data

import spire.random.Dist

final case class Prosody(phonology:Phonology)

object Prosody {
  import Phonology.instances._
  import PhonotacticRule.syntax._

  def syllableRule(prosody:Prosody):Dist[PhonotacticRule] = {
    val consonantRules = AnyPulmonic :: prosody.phonology.phonotactics.filterNot(_.hasVowel).toList
    val vowelRules = AnyVowel :: prosody.phonology.phonotactics.filter(_.hasVowel).toList
    for {
      cluster <- Dist.oneOf(consonantRules: _*)
      onset <- Dist.oneOf(Empty, cluster)
      rhyme <- Dist.oneOf(vowelRules: _*)
    } yield if (rhyme.startsWithVowel) {
      Concat(List(onset, rhyme))
    } else {
      rhyme
    }
  }

  def syllable(prosody:Prosody):Dist[Phones] =
    syllableRule(prosody)
      .flatMap(Phonology.applyRule(prosody.phonology, _))

  trait Instances {
    implicit val prosodyHasDist:Dist[Prosody] = Dist[Phonology].map(Prosody.apply)
  }
  object instances extends Instances
}
