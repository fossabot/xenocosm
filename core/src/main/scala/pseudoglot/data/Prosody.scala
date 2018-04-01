package pseudoglot
package data

import spire.random.Dist

final case class Prosody(phonology:Phonology)

object Prosody {
  import PhonotacticRule.syntax._

  def syllableRules(prosody:Prosody):Dist[Seq[PhonotacticRule]] =
    Phonology.anyRule(prosody.phonology)
      .flatMap({
        case rhyme if rhyme.hasVowel => Dist.constant(Seq(rhyme))
        case onset => Phonology.ruleWithVowel(prosody.phonology).map(rhyme => Seq(onset, rhyme))
      })

  def syllable(prosody:Prosody):Dist[Seq[Phone]] =
    syllableRules(prosody)
      .flatMap(Phonology.applyRules(prosody.phonology, _))
}
