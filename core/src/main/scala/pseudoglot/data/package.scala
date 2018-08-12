package pseudoglot

package object data {
  type Phones = List[Phone]
  type PhonotacticRules = Set[PhonotacticRule]
  type Transcription = Map[Phones, String]
}
