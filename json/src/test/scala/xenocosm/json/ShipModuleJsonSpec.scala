package xenocosm.json

import io.circe.syntax._
import xenocosm.data.ShipModule

class ShipModuleJsonSpec extends xenocosm.test.XenocosmSuite {
  import ShipModule.instances._
  import shipModule._

  test("ShipModule.json.isomorphism") {
    forAll { (a:ShipModule) =>
      a.asJson.as[ShipModule] shouldBe Right(a)
    }
  }
}
