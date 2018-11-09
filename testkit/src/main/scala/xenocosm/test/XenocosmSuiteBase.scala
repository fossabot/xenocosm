package xenocosm.test

import cats.Eq
import org.scalacheck.Cogen
import org.scalatest.Matchers
import org.scalatest.prop.PropertyChecks

trait XenocosmSuiteBase extends Matchers with PropertyChecks {
  implicit def eqCogenFromHashCode[T:Eq]:Cogen[T] = Cogen[Int].contramap(_.hashCode)
}
