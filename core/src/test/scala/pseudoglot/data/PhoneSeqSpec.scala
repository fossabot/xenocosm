package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import spire.random.Dist

class PhoneSeqSpec extends xenocosm.test.XenocosmSuite {
  import PhoneSeq.instances._
  import PhoneSeq.syntax._

  private val pulmonics = IPA.pulmonics.keys.toVector
  private val vowels = IPA.vowels.keys.toVector
  private val distArr:Dist[Array[Option[Phone]]] =
    for {
      pSize <- Dist.intrange(0, pulmonics.size)
      ps <- pulmonics.distPulmonic.pack(pSize)
      vSize <- Dist.intrange(0, vowels.size)
      vs <- vowels.distVowel.pack(vSize)
    } yield ps ++ vs
  implicit private val dist:Dist[Seq[Phone]] = distArr.map(_.flatten.toSeq)

  checkAll("Eq[PhoneSeq]", EqTests[Seq[Phone]].eqv)
}
