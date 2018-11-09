package xenocosm.json

import io.circe.{CursorOp, DecodingFailure, Json}
import io.circe.syntax._
import org.scalacheck.Arbitrary

import xenocosm.data.ShipModule

class ShipModuleJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import shipModule._

  implicit val arb:Arbitrary[ShipModule] = Arbitrary(xenocosm.gen.shipModule)

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
