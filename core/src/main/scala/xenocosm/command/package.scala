package xenocosm

import cats.data.{NonEmptyList, Validated}

import xenocosm.error.XenocosmError

package object command {
  type ValidatedCommand[A] = Validated[NonEmptyList[XenocosmError], A]
}
