package xenocosm
package json

import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.data.Cargo

class CargoJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import cargo._

  implicit val arb:Arbitrary[Cargo] = Arbitrary(xenocosm.gen.cargo)

  test("Cargo.json.isomorphism") {
    forAll { a:Cargo =>
      a.asJson.as[Cargo] shouldBe Right(a)
    }
  }
}
