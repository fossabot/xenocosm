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
    with data.Transcription.Instances
    with data.Phones.Syntax
}
