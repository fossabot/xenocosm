package pseudoglot
package data

import cats.kernel.laws.discipline.EqTests
import spire.random.Dist

class PhonesSpec extends xenocosm.test.XenocosmSuite {
  import Phone.instances._
  import Phones.instances._

  private val distPhone:Dist[Phone] =
    for {
      pulmonic <- Dist[Pulmonic]
      vowel <- Dist[Vowel]
      phone <- Dist.oneOf(pulmonic, vowel)
    } yield phone
  private implicit val dist:Dist[Phones] = distPhone.pack(8).map(_.toList)

  checkAll("Eq[Phones]", EqTests[Phones].eqv)
}
