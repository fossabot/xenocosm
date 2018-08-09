package xenocosm
package json

import io.circe.syntax._

import xenocosm.data.Cargo

class CargoJsonSpec extends xenocosm.test.XenocosmSuite {
  import Cargo.instances._
  import cargo._

  test("Cargo.json.isomorphism") {
    forAll { a:Cargo =>
      a.asJson.as[Cargo] shouldBe Right(a)
    }
  }
}
