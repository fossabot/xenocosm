package xenocosm.http
package hal

import io.circe.syntax._
import spire.random.Dist

import xenocosm.http.data.MultiverseResponse

class MultiverseResponseJsonSpec extends xenocosm.test.XenocosmSuite {
  import multiverse._

  implicit val dist:Dist[MultiverseResponse.type] = Dist.constant(MultiverseResponse)

  test("MultiverseResponse.json.isomorphism") {
    forAll { (a:MultiverseResponse.type) =>
      a.asJson.as[MultiverseResponse.type] shouldBe Right(a)
    }
  }
}
