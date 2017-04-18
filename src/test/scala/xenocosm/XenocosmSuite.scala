package xenocosm

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.scalatest.Discipline

trait XenocosmSuite extends FunSuite
  with Matchers
  with PropertyChecks
  with Discipline
