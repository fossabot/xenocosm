package pseudoglot.json

import io.circe.syntax._
import pseudoglot.data.{Magic, Phonology}
import spire.random.Dist

class PhonologyJsonSpec extends xenocosm.test.XenocosmSuite {
  import Magic.default
  import phonology._

  implicit val dist:Dist[Phonology] = Phonology.dist

  test("Phonology.json.isomorphism") {
    forAll { a:Phonology =>
      a.asJson.as[Phonology] shouldBe Right(a)
    }
  }
}
