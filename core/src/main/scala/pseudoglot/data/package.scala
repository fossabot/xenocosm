package pseudoglot

import cats.data.NonEmptyList

package object data {
  type Phones = NonEmptyList[Phone]
  type PhonotacticRules = NonEmptyList[PhonotacticRule]
  type Transcription = Map[Phones, String]
}
