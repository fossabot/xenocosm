package xenocosm.http
package data

import java.util.UUID
import galaxique.data.Universe
import org.http4s.dsl.impl._

class UuidSegmentSpec extends xenocosm.test.XenocosmSuite {

  test("UuidSegment.path.unapply") {
    val universe1 = Universe(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    val path = Path(s"/${universe1.uuid.toString}")
    path match {
      case Root / ♠(universe2) => universe1 shouldBe universe2
      case _ => fail()
    }
  }
}
