package pseudoglot

import cats.data.NonEmptyList

package object data {
  type Phones = NonEmptyList[Phone]
  type Transcription = Map[Phones, String]
}
