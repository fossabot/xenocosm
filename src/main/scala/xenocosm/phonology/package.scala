package xenocosm

package object phonology {
  type Romanization = Seq[data.Phone] ⇒ String

  object instances extends PhonotacticRuleNotation.Instances
                      with data.Backness.Instances
                      with data.Height.Instances
                      with data.Manner.Instances
                      with data.Place.Instances
                      with data.Roundedness.Instances
                      with data.Voicing.Instances
                      with data.Pulmonic.Instances
                      with data.Vowel.Instances
                      with data.Phonology.Instances
                      with data.PhonotacticRule.Instances

  object syntax extends PhonotacticRuleNotation.Syntax
                   with Romanization.Syntax
}
