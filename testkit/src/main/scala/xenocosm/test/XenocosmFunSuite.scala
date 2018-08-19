package xenocosm
package test

import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

trait XenocosmFunSuite extends FunSuite
  with XenocosmSuiteBase
  with Discipline
