package pseudoglot
package data

import spire.random.Dist

final case class Prosody(phonology:Phonology)

object Prosody {
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

  def dist(implicit magic:Magic):Dist[Prosody] = Phonology.dist.map(Prosody.apply)

  def syllable(prosody:Prosody):Dist[Phones] =
    syllableRule(prosody)
      .flatMap(Phonology.applyRule(prosody.phonology, _))
}
