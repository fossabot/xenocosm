package pseudoglot

package object json {
  object backness extends BacknessJson
  object height extends HeightJson
  object manner extends MannerJson
  object phonology extends PhonologyJson
  object phonotacticRule extends PhonotacticRuleJson
  object place extends PlaceJson
  object pulmonic extends PulmonicJson
  object roundedness extends RoundednessJson
  object transcription extends TranscriptionJson
  object voicing extends VoicingJson
  object vowel extends VowelJson

  object implicits extends BacknessJson
                      with HeightJson
                      with MannerJson
                      with PhonologyJson
                      with PhonotacticRuleJson
                      with PlaceJson
                      with PulmonicJson
                      with RoundednessJson
                      with TranscriptionJson
                      with VoicingJson
                      with VowelJson
}
