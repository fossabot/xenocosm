/**
  * <img src="/xenocosm/img/pseudoglot-128.png" />
  * Provides data types and typeclasses for procedurally-generated phonologies
  *
  * ==Overview==
  * As much as possible, all data types have an instance of `spire.random.Dist`
  * in the companion object.
  */
package object pseudoglot {

  object implicits extends data.Phonology.Instances
    with data.Backness.Instances
    with data.Height.Instances
    with data.Manner.Instances
    with data.Place.Instances
    with data.Roundedness.Instances
    with data.Voicing.Instances
    with data.Phone.Instances
    with data.Phones.Instances
    with data.PhonotacticRule.Instances
    with data.Prosody.Instances
    with data.Transcription.Instances
    with data.Phones.Syntax

  // Magic Constants
  val PULMONICS_MIN = 7
  val PULMONICS_MAX = 21
  val VOWELS_MIN    = 3
  val VOWELS_MAX    = 9
  val RULES_MIN     = 5
  val RULES_MAX     = 10

  val PROB_LITERAL = 0.25
  val PROB_CONCAT  = 0.45
  val PROB_CHOOSE  = 0.65
}
