package xenocosm.json

import io.circe.{CursorOp, DecodingFailure, Json}
import io.circe.syntax._

import xenocosm.data.ShipModule

class ShipModuleJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import ShipModule.instances._
  import shipModule._

  test("ShipModule.json.isomorphism") {
    forAll { a:ShipModule =>
      a.asJson.as[ShipModule] shouldBe Right(a)
    }
  }

  test("ShipModule.json.unrecognized-module") {
    Json.obj("module" -> Json.fromString("foo")).as[ShipModule] shouldBe Left(DecodingFailure(
      "unrecognized module",
      List.empty[CursorOp]
    ))
  }
}
