package xenocosm.http
package hal

import galaxique.data.{Point3, Universe}
import io.circe.syntax._
import spire.random.Dist
import squants.space.Parsecs

import xenocosm.http.data.UniverseResponse

class UniverseResponseJsonSpec extends xenocosm.test.XenocosmSuite {
  import Universe.instances._

  import universe._

  val origin = Point3(Parsecs(0), Parsecs(0), Parsecs(0))
  implicit val dist:Dist[UniverseResponse] =
    Dist[Universe].map(universe => UniverseResponse(universe, origin, Parsecs(10000)))

  test("UniverseResponse.json.isomorphism") {
    forAll { (a:UniverseResponse) =>
      a.asJson.as[UniverseResponse] shouldBe Right(a)
    }
  }
}
